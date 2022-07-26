package net.hollowcube.blocks.ore.handler;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hollowcube.blocks.ore.Ore;
import net.hollowcube.blocks.ore.event.PlayerOreBreakEvent;
import net.hollowcube.loot.LootContext;
import net.hollowcube.server.instance.TickTrackingInstance;
import net.hollowcube.util.BlockUtil;
import net.hollowcube.util.FutureUtil;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Stateless singleton block handler for all ore blocks.
 */
public class OreBlockHandler implements BlockHandler {
    private static final OreBlockHandler instance = new OreBlockHandler();

    public static @NotNull OreBlockHandler instance() {
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OreBlockHandler.class);

    //todo this should probably be configurable.
    public static final Block REPLACEMENT_BLOCK = Block.BEDROCK;

    private OreBlockHandler() {
        Check.stateCondition(instance != null, "There can only be one OreBlockHandler");
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        final Instance instance = placement.getInstance();
        final Point pos = placement.getBlockPosition();
        final Block block = placement.getBlock();

        // Ensure the block is an ore
        final Ore ore = Ore.fromBlock(block);
        if (ore == null) {
            // Block handler should not be present if the ore tag is not present. Remove it and log error.
            LOGGER.error("Handler present on a block without ore tag: {} at {} in {}", block, pos, instance);
            instance.setBlock(pos, block.withHandler(null));
            return;
        }

        // Do nothing if the block is the replacement
        if (block.compare(REPLACEMENT_BLOCK, Block.Comparator.STATE))
            return;

        // Ensure the block has the right state
        if (block.compare(ore.oreBlock(), Block.Comparator.STATE))
            return;

        // Block should already be configured, if not we have a bug.
        LOGGER.error("Ore block has wrong state: {} at {} in {}", block, pos, instance);
        instance.setBlock(pos, BlockUtil.withType(block, ore.oreBlock()));
    }

    @Override
    public void onDestroy(@NotNull Destroy d) {
        final Block block = d.getBlock();
        if (block.compare(REPLACEMENT_BLOCK, Block.Comparator.STATE))
            return;
        if (!(d instanceof PlayerDestroy destroy)) {
            return;
        }

        // Get the Ore definition of the block being broken
        final Instance instance = destroy.getInstance();
        final Point pos = destroy.getBlockPosition();
        final Ore ore = Ore.fromBlock(block);
        if (ore == null) {
            // Block handler should not be present if the ore tag is not present. Remove it and log error.
            LOGGER.error("Handler present on a block without ore tag: {} at {} in {}", block, pos, instance);
            instance.setBlock(pos, block.withHandler(null));
            return;
        }

        // Call break event
        final Player player = destroy.getPlayer();
        final var event = new PlayerOreBreakEvent(player, block, ore);
        EventDispatcher.call(event);

        // Replace the block with the temporary replacement block
        instance.setBlock(pos, BlockUtil.withType(block, REPLACEMENT_BLOCK));

        // Generate loot
        final var direction = destroy.getBlockFace().toDirection();
        final var context = LootContext.builder("mining")
                .key(LootContext.THIS_ENTITY, player)
                .key(LootContext.POSITION, pos)
                .key(LootContext.DIRECTION, new Vec(direction.normalX(), direction.normalY(), direction.normalZ()))
                .build();
        final var loot = ore.lootTable().generate(context);
        // Distribute loot
        loot.apply(context).exceptionally(FutureUtil::handleException);
    }

    @Override
    public void tick(@NotNull Tick tick) {
        final Block block = tick.getBlock();
        // Only tick on the replacement block
        if (!block.compare(REPLACEMENT_BLOCK, Block.Comparator.STATE))
            return;

        // Only tick once a second.
        // This could be adjusted in the future if necessary.
        final Instance instance = tick.getInstance();
        long currentTick = ((TickTrackingInstance) instance).getTick();
        if (currentTick % 20 != 0)
            return;

        final Point pos = tick.getBlockPosition();
        final Ore ore = Ore.fromBlock(block);
        if (ore == null) {
            // Block handler should not be present if the ore tag is not present. Remove it and log error.
            LOGGER.error("Handler present on a block without ore tag: {} at {} in {}", block, pos, instance);
            instance.setBlock(pos, block.withHandler(null));
            return;
        }

        // 25% chance every time it ticks, so once per second.
        if (ThreadLocalRandom.current().nextInt(4) != 0)
            return;

        // Set the block back to the original
        instance.setBlock(pos, BlockUtil.withType(block, ore.oreBlock()));
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("starlight:ore");
    }

    @Override
    public boolean isTickable() {
        return true;
    }
}
