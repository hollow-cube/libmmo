package unnamed.mmo.item.loot;

import com.google.auto.service.AutoService;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.entity.OwnedItemEntity;
import unnamed.mmo.loot.LootResult;
import unnamed.mmo.loot.context.ContextKeys;
import unnamed.mmo.loot.context.LootContext;

@AutoService(LootResult.DefaultDistributor.class)
public class ItemDistributor implements LootResult.DefaultDistributor<Item> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDistributor.class);

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:item");
    }

    @Override
    public @NotNull Class<Item> type() {
        return Item.class;
    }

    @Override
    public void apply(@NotNull LootContext context, @NotNull Item item) {
        final Entity entity = context.get(ContextKeys.THIS_ENTITY);
        if (entity == null) {
            LOGGER.error("No `this` entity for item distributor. context={}", context);
            return;
        }

        final ItemStack itemStack = item.asItemStack();
        final OwnedItemEntity itemEntity = new OwnedItemEntity(entity.getUuid(), itemStack);

        // Spawn at the hinted location, or the entity location if not provided.
        Point pos = context.get(ContextKeys.POSITION);
        if (pos == null) pos = entity.getPosition();
        // Set the location to the center of the block
        pos = new Vec(pos.blockX() + 0.5f, pos.blockY() + 0.5f, pos.blockZ() + 0.5f);

        // Spawn the entity at the location
        itemEntity.setInstance(entity.getInstance(), pos);

        // Spawn with a velocity in the hinted direction, if present
        Vec direction = context.get(ContextKeys.DIRECTION);
        if (direction != null) {
            direction = direction.normalize().mul(3f);
            itemEntity.setVelocity(direction);
        }
    }

}
