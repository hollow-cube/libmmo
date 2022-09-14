package net.hollowcube.item.entity;

import com.google.auto.service.AutoService;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityItemMergeEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;

import java.util.UUID;

/**
 * An {@link OwnedItemEntity} is a regular {@link ItemEntity}, except that it may only be picked up by the defined
 * owner. This should be used for all items currently.
 */
public class OwnedItemEntity extends ItemEntity {
    private final UUID owner;

    /**
     * @param owner     The uuid of the owning entity (player or otherwise)
     * @param itemStack The item stack to spawn
     */
    public OwnedItemEntity(@NotNull UUID owner, @NotNull ItemStack itemStack) {
        super(itemStack);
        this.owner = owner;
    }


    @AutoService(Facet.class)
    public static class Handler implements Facet {
        @Override
        public void hook(@NotNull ServerWrapper server) {
            var eventNode = EventNode.all("unnamed:item_entity/handler");
            eventNode.addListener(PickupItemEvent.class, this::handlePickup);
            eventNode.addListener(EntityItemMergeEvent.class, this::handleMerge);
            server.addEventNode(eventNode);
        }

        private void handlePickup(@NotNull PickupItemEvent event) {
            if (!(event.getItemEntity() instanceof OwnedItemEntity itemEntity))
                return;

            // Only players can pick up items for now, eventually entities may be allowed
            if (!(event.getEntity() instanceof Player player)) {
                event.setCancelled(true);
                return;
            }

            // Ensure owner is the one picking up the item
            if (!player.getUuid().equals(itemEntity.owner)) {
                event.setCancelled(true);
                return;
            }

            // Add item to the player if possible
            boolean added = player.getInventory().addItemStack(event.getItemStack());
            if (!added) {
                event.setCancelled(true);
            }
        }

        private void handleMerge(@NotNull EntityItemMergeEvent event) {
            if (!(event.getEntity() instanceof OwnedItemEntity entity) ||
                    !(event.getMerged() instanceof OwnedItemEntity merged))
                return;
            if (!entity.owner.equals(merged.owner))
                event.setCancelled(true);
        }
    }
}
