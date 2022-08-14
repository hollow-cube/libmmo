package unnamed.mmo.item.entity;

import com.google.auto.service.AutoService;
import net.minestom.server.ServerProcess;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
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

            final LivingEntity entity = event.getLivingEntity();
            if (!entity.getUuid().equals(itemEntity.owner)) {
                event.setCancelled(true);
            }
        }

        private void handleMerge(@NotNull EntityItemMergeEvent event) {
            if (!(event.getEntity() instanceof OwnedItemEntity entity) ||
                    !(event.getMerged() instanceof OwnedItemEntity merged))
                return;
            if (entity.owner.equals(merged.owner))
                event.setCancelled(true);
        }
    }
}
