package unnamed.mmo.fishing.item;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.blocks.util.BlockUtil;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemComponentHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

@AutoService(ItemComponentHandler.class)
public class FishingRodHandler implements ItemComponentHandler<FishingRod> {

    // Weakly keep track of the fishing hooks -- weak to handle cases of players leaving or entities being destroyed
    private static final WeakHashMap<Player, List<Entity>> FISHING_HOOKS = new WeakHashMap<>();

    private final EventNode<Event> eventNode = EventNode.all("unnamed:fishing_rod/item_component_handler");

    public FishingRodHandler() {
        eventNode.addListener(PlayerUseItemEvent.class, this::useItemOnAir);
        eventNode.addListener(PlayerChangeHeldSlotEvent.class, this::changeHeldSlot);
    }

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:fishing_rod");
    }

    @Override
    public @NotNull Class<FishingRod> componentType() {
        return FishingRod.class;
    }

    @Override
    public @NotNull Codec<@NotNull FishingRod> codec() {
        return FishingRod.CODEC;
    }

    @Override
    public @Nullable EventNode<Event> eventNode() {
        return eventNode;
    }

    private void bobberTick(@NotNull Entity bobberEntity, boolean wasInWater) {
        if (bobberEntity.isRemoved() || bobberEntity.getInstance() == null) return;

        var position = bobberEntity.getPosition();

        var inWater = BlockUtil.isWater(bobberEntity.getInstance().getBlock(position));

        if (inWater && !wasInWater) {
            bobberEntity.getInstance().playSound(Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_SPLASH, Sound.Source.PLAYER, 0.5f, 1f), position.x(), position.y(), position.z());
        }

        if (inWater) {
            bobberEntity.setVelocity(bobberEntity.getVelocity().add(0, 0.68, 0));
        }

        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> bobberTick(bobberEntity, inWater));
    }

    private void summonBobber(@NotNull Instance instance, @NotNull Player player, @NotNull FishingRod rod) {

        FISHING_HOOKS.put(player, new ArrayList<>());

        for (int i = 0; i < rod.bobberCount(); i++) {
            var bobber = new Entity(EntityType.FISHING_BOBBER);

            FISHING_HOOKS.get(player).add(bobber);

            var bobberMeta = (FishingHookMeta) bobber.getEntityMeta();

            // Need to connect it to the owner's fishing rod
            bobberMeta.setOwnerEntity(player);

            bobber.setInstance(instance, player.getPosition().add(0.0, player.getEyeHeight(), 0.0)).thenRun(() -> {
                bobber.setVelocity(player.getPosition().direction().normalize().mul(rod.strength()));
                bobberTick(bobber, false);
            });
        }

        var position = player.getPosition();
        instance.playSound(Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_THROW, Sound.Source.PLAYER, 1f, 0.5f), position.x(), position.y(), position.z());
    }

    private void removeBobbers(Player player, Instance instance) {
        for (var hook : FISHING_HOOKS.get(player)) {
            // Remove all fishing hooks
            hook.remove();
        }

        var position = player.getPosition();
        instance.playSound(Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_RETRIEVE, Sound.Source.PLAYER, 1f, 1f), position.x(), position.y(), position.z());

        // Remove fishing hooks from the map
        FISHING_HOOKS.remove(player);
    }

    private void changeHeldSlot(@NotNull PlayerChangeHeldSlotEvent event) {
        if (event.getSlot() != event.getPlayer().getHeldSlot()) {
            removeBobbers(event.getPlayer(), event.getInstance());
        }
    }

    private void useItemOnAir(@NotNull PlayerUseItemEvent event) {
        var itemStack = event.getPlayer().getItemInHand(event.getHand());

        var item = Item.fromItemStack(itemStack);

        var rod = item.getComponent(FishingRod.class);

        if (rod == null) return; // No fishing rod in hand

        if (FISHING_HOOKS.get(event.getPlayer()) != null) { // Fishing rod is already thrown
            removeBobbers(event.getPlayer(), event.getInstance());
        } else {
            summonBobber(event.getInstance(), event.getPlayer(), rod);
        }
    }
}