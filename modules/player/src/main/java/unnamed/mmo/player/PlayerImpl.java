package unnamed.mmo.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.modifiers.ModifierList;
import unnamed.mmo.modifiers.ModifierOperation;
import unnamed.mmo.modifiers.ModifierType;
import unnamed.mmo.player.event.PlayerLongDiggingStartEvent;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntSupplier;

public class PlayerImpl extends Player {

    // Long digging state
    private Point diggingBlock = null;
    private IntSupplier diggingDamageFn = null;
    private int diggingLastStage = 0;
    private int diggingBlockHealth = 0;
    private int diggingBlockMaxHealth = 0;
    private BlockFace diggingFace = null;

    private final Map<String, ModifierList> currentModifiers = new HashMap<>();


    public PlayerImpl(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        eventNode().addListener(PlayerPacketEvent.class, this::handlePacket);
    }

    @Override
    public void update(long time) {
        super.update(time);

        tickLongDigging();
    }

    private void handlePacket(PlayerPacketEvent event) {
        ClientPacket packet = event.getPacket();

        if (packet instanceof ClientPlayerDiggingPacket diggingPacket) {
            handleDiggingPacket(diggingPacket);
        }
    }

    private void handleDiggingPacket(ClientPlayerDiggingPacket packet) {
        switch (packet.status()) {
            case STARTED_DIGGING -> {
                final Instance instance = getInstance();
                if (instance == null) return;
                final Block block = getInstance().getBlock(packet.blockPosition());

                // Call event
                PlayerLongDiggingStartEvent event = new PlayerLongDiggingStartEvent(this, block);
                MinecraftServer.getGlobalEventHandler().call(event);

                // Setup internal state for digging
                if (event.getMaxHealth() != 0) {
                    diggingBlock = packet.blockPosition();
                    diggingDamageFn = event.getDamageFunction();
                    diggingBlockHealth = event.getMaxHealth();
                    diggingBlockMaxHealth = event.getMaxHealth();
                    diggingLastStage = 0;
                    diggingFace = packet.blockFace();
                }
            }
            case CANCELLED_DIGGING -> clearLongDigging();
            case FINISHED_DIGGING -> {
                // This would only happen if the player somehow lost mining fatigue (or is running a hacked client)
                //todo need to cancel this i guess?
                // Also if this happens it means the client has lost mining fatigue, so we should give it back to them
                // unless they are in the bypass mode that might exist on the dev server
                clearLongDigging();
            }
            default -> {}
        }
    }

    private void tickLongDigging() {
        if (diggingBlock == null) return;

        int damage = diggingDamageFn.getAsInt();

        diggingBlockHealth = Math.max(0, diggingBlockHealth - damage);
        if (diggingBlockHealth == 0) {
            // Break the block & reset
            getInstance().breakBlock(this, diggingBlock, diggingFace);
            clearLongDigging();
        } else {
            updateDiggingBlock();
        }
    }

    private void clearLongDigging() {
        diggingBlockHealth = 0;
        diggingBlockMaxHealth = 0;
        diggingLastStage = 0;
        diggingFace = null;

        // Send update to clear digging animation
        updateDiggingBlock();
        diggingBlock = null;
    }

    private void updateDiggingBlock() {
        if (diggingBlock == null) return;

        byte stage = Byte.MAX_VALUE;
        if (diggingBlockMaxHealth != 0) {
            stage = (byte) (((float) (diggingBlockMaxHealth - diggingBlockHealth) / diggingBlockMaxHealth) * 12f);
            if (stage == diggingLastStage) return;
            diggingLastStage = stage;
        }

        // New stage, send packet
        var packet = new BlockBreakAnimationPacket(getEntityId() + 1, diggingBlock, stage);
        sendPacket(packet);
    }

    // ----- MODIFIERS -----

    /**
     * Adds a new permanent modifier to this player
     * @param modifierType The type of modifier to modify, such as dig speed, melee damage, etc. This modifier type should be in the modifier registry, and can be checked with {@link ModifierType#doesModifierExist(String)}
     * @param modifierId The id of this modifier. If a player already has this modifier id, it will be overridden. This id can be basically any string, and it should be unique to each source so modifiers can stack properly
     * @param amount The amount to modify by
     * @param operation The operation with which to modify by
     */
    public void addPermanentModifier(String modifierType, String modifierId, double amount, ModifierOperation operation) {
        if (ModifierType.doesModifierExist(modifierType)) {
            currentModifiers.putIfAbsent(modifierType, new ModifierList(ModifierType.getBaseValue(modifierType)));
            currentModifiers.get(modifierType).addPermanentModifier(modifierId, amount, operation);
        }
    }

    /**
     * Adds a new temporary modifier to this player
     * @param modifierType The type of modifier to modify, such as dig speed, melee damage, etc. This modifier type should be in the modifier registry, and can be checked with {@link ModifierType#doesModifierExist(String)}
     * @param modifierId The id of this modifier. If a player already has this modifier id, it will be overridden. This id can be basically any string, and it should be unique to each source so modifiers can stack properly
     * @param amount The amount to modify by
     * @param operation The operation with which to modify by
     * @param expireTime The duration this modifier lasts
     */
    public void addTemporaryModifier(String modifierType, String modifierId, double amount, ModifierOperation operation, long expireTime) {
        if (ModifierType.doesModifierExist(modifierType)) {
            currentModifiers.putIfAbsent(modifierType, new ModifierList(ModifierType.getBaseValue(modifierType)));
            currentModifiers.get(modifierType).addTemporaryModifier(modifierId, amount, operation, System.currentTimeMillis() + expireTime);
        }
    }

    public double getModifierValue(String modifierType) {
        if (ModifierType.doesModifierExist(modifierType)) {
            if (currentModifiers.containsKey(modifierType)) {
                return currentModifiers.get(modifierType).calculateTotal();
            } else {
                return ModifierType.getBaseValue(modifierType);
            }
        }
        return -999;
    }

    public void removeModifier(String modifierType, String modifierId) {
        if(currentModifiers.containsKey(modifierType)) {
            currentModifiers.get(modifierType).removeModifier(modifierId);
        }
    }

    public Map<String, Double> getCurrentModifierValues() {
        HashMap<String, Double> amounts = new HashMap<>();
        for(var entry : currentModifiers.entrySet()) {
            amounts.put(entry.getKey(), entry.getValue().calculateTotal());
        }
        return amounts;
    }
}
