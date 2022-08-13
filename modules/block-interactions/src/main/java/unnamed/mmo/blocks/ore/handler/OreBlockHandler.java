package unnamed.mmo.blocks.ore.handler;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unnamed.mmo.blocks.ore.Ore;
import unnamed.mmo.loot.LootTable;
import unnamed.mmo.util.BlockUtil;

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
    private static final Block REPLACEMENT_BLOCK = Block.BEDROCK;

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
    public void onDestroy(@NotNull Destroy destroy) {
        final Block block = destroy.getBlock();
        if (block.compare(REPLACEMENT_BLOCK, Block.Comparator.STATE) || !(destroy instanceof PlayerDestroy))
            return;

        final Instance instance = destroy.getInstance();
        final Point pos = destroy.getBlockPosition();
        final Ore ore = Ore.fromBlock(block);
        if (ore == null) {
            // Block handler should not be present if the ore tag is not present. Remove it and log error.
            LOGGER.error("Handler present on a block without ore tag: {} at {} in {}", block, pos, instance);
            instance.setBlock(pos, block.withHandler(null));
            return;
        }

        // Replace the block with the temporary replacement block
        instance.setBlock(pos, BlockUtil.withType(block, REPLACEMENT_BLOCK));

        // Generate loot
        //todo use a real context
        final var loot = ore.lootTable().generate(() -> 1);

        //todo apply loot
        // this is a little bit challenging, we want to apply the loot entries based on
        // a registered handler for the given type, except in this case we want to
        // override this to handle items explicitly.

        // The item override is just to give the items some velocity on a block face
        //  this could be handled within the item drop handler, using some context
        //  information.

        // Context in this case
        // this: entity breaking the block
        // pos: the position of the block



    }

    @Override
    public void tick(@NotNull Tick tick) {
        final Block block = tick.getBlock();
        // Only tick on the replacement block
        if (!block.compare(REPLACEMENT_BLOCK, Block.Comparator.STATE))
            return;

        // Only tick once a second.
        // This could be adjusted in the future if necessary.
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("unnamed:ore");
    }

    @Override
    public boolean isTickable() {
        return true;
    }
}