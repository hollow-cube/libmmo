package unnamed.mmo.fishing.item;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    private void summonBobber(@NotNull Instance instance, @NotNull Player player, @NotNull FishingRod rod) {

        FISHING_HOOKS.put(player, new ArrayList<>());

        for (int i = 0; i < rod.bobberCount(); i++) {
            var bobber = new Entity(EntityType.FISHING_BOBBER);

            FISHING_HOOKS.get(player).add(bobber);

            var bobberMeta = (FishingHookMeta) bobber.getEntityMeta();

            // Need to connect it to the owner's fishing rod
            bobberMeta.setOwnerEntity(player);

            bobber.setInstance(instance, player.getPosition().add(0.0, player.getEyeHeight(), 0.0));
                    bobber.setVelocity(player.getPosition().direction().normalize().mul(rod.strength()));
        }

        player.playSound(Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_THROW, Sound.Source.PLAYER, 1f, 1f));
    }

    private void useItemOnAir(@NotNull PlayerUseItemEvent event) {
        var itemStack = event.getPlayer().getItemInHand(event.getHand());

        var item = Item.fromItemStack(itemStack);

        var rod = item.getComponent(FishingRod.class);

        if (rod == null) return; // No fishing rod in hand

        if (FISHING_HOOKS.get(event.getPlayer()) != null) { // Fishing rod is already thrown
            for (var hook : FISHING_HOOKS.get(event.getPlayer())) {
                // Remove all fishing hooks
                hook.remove();
            }

            event.getPlayer().playSound(Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_RETRIEVE, Sound.Source.PLAYER, 1f, 1f));

            // Remove fishing hooks from the map
            FISHING_HOOKS.remove(event.getPlayer());
        } else {
            summonBobber(event.getInstance(), event.getPlayer(), rod);
        }
    }
}