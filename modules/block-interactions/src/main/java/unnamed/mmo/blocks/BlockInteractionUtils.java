package unnamed.mmo.blocks;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.blocks.handlers.CropBlockData;

public class BlockInteractionUtils {
    private static final String DOMAIN_NAME = "unnammedmmo";

    public static final NamespaceID TILL_HANDLER_ID = NamespaceID.from(DOMAIN_NAME, "tillhandler");
    public static final NamespaceID FARMLAND_HANDLER_ID = NamespaceID.from(DOMAIN_NAME, "farmlandhandler");
    public static final NamespaceID CROP_HANDLER_ID = NamespaceID.from(DOMAIN_NAME, "crophandler");


    public static Block storeDataOntoBlock(Block block, CropBlockData data) {
        return block
                .withTag(Tag.Integer("seedMaterial"), data.seedMaterial().id())
                .withTag(Tag.Integer("cropBlockMaterial"), data.cropBlockMaterial().id())
                .withTag(Tag.Integer("maximumAge"), data.maximumAge())
                .withTag(Tag.Boolean("createsAnotherBlock"), data.createAnotherBlock());
    }

    /**
     * Reads the stored CropBlockData from a block
     * @param block - the block to read the data from
     * @return The stored CropBlockData, or null if the block does not have the proper data
     */
    public static @Nullable CropBlockData readDataFromBlock(Block block) {
        if(block.hasTag(Tag.Integer("seedMaterial"))) {
            return new CropBlockData(
                    Material.fromId(block.getTag(Tag.Integer("seedMaterial"))),
                    Material.fromId(block.getTag(Tag.Integer("cropBlockMaterial"))),
                    block.getTag(Tag.Integer("maximumAge")),
                    block.getTag(Tag.Boolean("createsAnotherBlock"))
            );
        } else {
            return null;
        }
    }
}
