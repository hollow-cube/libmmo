package unnamed.mmo.blocks;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.blocks.data.CropBlockData;
import unnamed.mmo.blocks.data.CropBlockDataSerializer;
import unnamed.mmo.blocks.handlers.CropHandler;
import unnamed.mmo.blocks.handlers.FarmlandHandler;

public class BlockInteractionUtils {
    private static final String DOMAIN_NAME = "unnammedmmo";

    public static final NamespaceID TILL_HANDLER_ID = NamespaceID.from(DOMAIN_NAME, "tillhandler");
    public static final NamespaceID FARMLAND_HANDLER_ID = NamespaceID.from(DOMAIN_NAME, "farmlandhandler");
    public static final NamespaceID CROP_HANDLER_ID = NamespaceID.from(DOMAIN_NAME, "crophandler");


    public static void registerHandlers() {
        MinecraftServer.getBlockManager().registerHandler(FARMLAND_HANDLER_ID, FarmlandHandler::new);
        MinecraftServer.getBlockManager().registerHandler(CROP_HANDLER_ID, CropHandler::new);
    }


    private static final Tag<CropBlockData> cropBlockDataTag = Tag.Structure("CropBlockData", new CropBlockDataSerializer());

    public static @NotNull Block storeDataOntoBlock(@NotNull Block block, @NotNull CropBlockData data) {
        return block.withTag(cropBlockDataTag, data);
    }

    /**
     * Reads the stored CropBlockData from a block
     * @param block - the block to read the data from
     * @return The stored CropBlockData, or null if the block does not have the proper data
     */
    public static @Nullable CropBlockData readDataFromBlock(@NotNull Block block) {
        if (block.hasTag(cropBlockDataTag)) {
            return block.getTag(cropBlockDataTag);
        } else {
            return null;
        }
    }
}
