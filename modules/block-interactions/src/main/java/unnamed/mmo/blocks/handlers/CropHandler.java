package unnamed.mmo.blocks.handlers;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.BlockInteractionUtils;

public class CropHandler implements BlockHandler {

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        CropBlockData blockData = BlockInteractionUtils.readDataFromBlock(destroy.getBlock());
        if(blockData == null) return;

        if(blockData.maximumAge() == getCurrentAge(destroy.getBlock()) && !blockData.createAnotherBlock()) {
            Entity entity = new Entity(EntityType.ITEM);
            if(entity.getEntityMeta() instanceof ItemEntityMeta itemEntityMeta) {
                itemEntityMeta.setItem(ItemStack.of(blockData.cropBlockMaterial()));
            }
            entity.setInstance(destroy.getInstance(), destroy.getBlockPosition().add(0.5, 0.5, 0.5));
        }
        Entity entity = new Entity(EntityType.ITEM);
        if(entity.getEntityMeta() instanceof ItemEntityMeta itemEntityMeta) {
            itemEntityMeta.setItem(ItemStack.of(blockData.seedMaterial()));
        }
        entity.setInstance(destroy.getInstance(), destroy.getBlockPosition().add(0.5, 0.5, 0.5));
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        // Depends on what we want to do for this, do we want the ability to right-click harvest like a bunch of mods have?
        return false;
    }

    private final int cropUpdateThreshold = 40*20;
    private int cropUpdateCounter = 0;

    @Override
    public void tick(@NotNull Tick tick) {
        cropUpdateCounter++;
        if(cropUpdateCounter >= cropUpdateThreshold) {
            cropUpdateCounter = 0;
            CropBlockData blockData = BlockInteractionUtils.readDataFromBlock(tick.getBlock());
            if(blockData == null) return;

            int age = getCurrentAge(tick.getBlock());
            if(age == -1) {
                //TODO Log error?
                return;
            }
            if(age == blockData.maximumAge() && blockData.createAnotherBlock()) {
                // TODO: Specific cases for pumpkins/melons
            }
            if(age < blockData.maximumAge()) {
                tick.getInstance().setBlock(tick.getBlockPosition(), tick.getBlock().withProperty("age", Integer.toString(++age)));
            }
        }
    }

    private int getCurrentAge(@NotNull Block block) {
        if(block.getProperty("age") != null) {
            return Integer.parseInt(block.getProperty("age"));
        } else {
            return -1;
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return BlockInteractionUtils.CROP_HANDLER_ID;
    }
}
