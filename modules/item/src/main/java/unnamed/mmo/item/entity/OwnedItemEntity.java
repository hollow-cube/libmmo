package unnamed.mmo.item.entity;

import com.google.auto.service.AutoService;
import net.minestom.server.ServerProcess;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityItemMergeEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.server.Facet;

import java.util.UUID;

public class OwnedItemEntity extends ItemEntity {
    private final UUID owner;

    public OwnedItemEntity(@NotNull UUID owner, @NotNull ItemStack itemStack) {
        super(itemStack);
        this.owner = owner;
    }


    @AutoService(Facet.class)
    public static class Handler implements Facet {
        @Override
        public void hook(@NotNull ServerProcess server) {
            var node = EventNode.all("owneditementity");
            node.addListener(PickupItemEvent.class, this::handlePickup);
            node.addListener(EntityItemMergeEvent.class, this::handleMerge);
            server.eventHandler().addChild(node);
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
