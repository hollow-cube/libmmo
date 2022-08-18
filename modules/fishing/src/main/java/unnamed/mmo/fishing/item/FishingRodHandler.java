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

@AutoService(ItemComponentHandler.class)
public class FishingRodHandler implements ItemComponentHandler<FishingRod> {

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
        for (int i = 0; i < rod.bobberCount(); i++) {
            var bobber = new Entity(EntityType.FISHING_BOBBER);

            var bobberMeta = (FishingHookMeta) bobber.getEntityMeta();

            // Need to connect it to the owner's fishing rod
            bobberMeta.setOwnerEntity(player);

            bobber.setInstance(instance, player.getPosition().add(0.0, player.getEyeHeight(), 0.0));

            bobber.setVelocity(player.getPosition().direction().normalize().mul(rod.strength()));
        }

        player.playSound(Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_THROW, Sound.Source.PLAYER, 1f, 1f));
    }

    private void useItemOnAir(@NotNull PlayerUseItemEvent event) {
        var item = Item.fromItemStack(event.getPlayer().getItemInMainHand());

        var rod = item.getComponent(FishingRod.class);

        if (rod == null) return; // No fishing rod in hand

        summonBobber(event.getInstance(), event.getPlayer(), rod);
    }
}