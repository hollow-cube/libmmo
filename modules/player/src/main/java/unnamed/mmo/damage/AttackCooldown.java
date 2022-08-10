package unnamed.mmo.damage;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Player;
import net.minestom.server.network.socket.Server;

import java.util.HashMap;
import java.util.UUID;

public class AttackCooldown {

    private final HashMap<UUID, TickEntry> cooldownTime = new HashMap<>();

    public void resetCooldown(Player player) {
        // From https://minecraft.fandom.com/wiki/Damage#Attack_cooldown
        float attackSpeed = player.getAttributeValue(Attribute.ATTACK_SPEED);
        int cooldownTicks = (int) (20f / attackSpeed);
        cooldownTime.put(player.getUuid(), new TickEntry(System.currentTimeMillis(), cooldownTicks));
    }

    public double getCooldownDamageMultiplier(Player player) {
        // Formula from https://minecraft.fandom.com/wiki/Damage#Attack_cooldown
        if(!cooldownTime.containsKey(player.getUuid())) {
            return 1;
        }
        float attackSpeed = player.getAttributeValue(Attribute.ATTACK_SPEED);
        int cooldownTicks = (int) (20f / attackSpeed);
        int ticksSinceLastAttacked = (int) ((System.currentTimeMillis() - cooldownTime.get(player.getUuid()).startTime) / MinecraftServer.TICK_MS);
        return (0.2 + Math.pow(((ticksSinceLastAttacked + 0.5) / cooldownTicks), 2) * 0.8);
    }

    public void update() {
        cooldownTime.entrySet().removeIf(entry -> entry.getValue().startTime + (long) MinecraftServer.TICK_MS * entry.getValue().ticksImmune < System.currentTimeMillis());
    }

    private record TickEntry(long startTime, int ticksImmune) {}
}