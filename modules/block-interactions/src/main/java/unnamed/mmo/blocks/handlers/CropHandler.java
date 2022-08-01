package unnamed.mmo.blocks.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.BlockInteractionUtils;

public class CropHandler implements BlockHandler {

    private final int maximumAge;

    public CropHandler(int maximumAge) {
        this.maximumAge = maximumAge;
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        // TODO: Drop seeds if not fully grown, drop seeds and crop if fully gornw
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
            String ageString = tick.getBlock().getProperty("age");
            int age = Integer.parseInt(ageString);
            if(age < maximumAge) {
                tick.getInstance().setBlock(tick.getBlockPosition(), tick.getBlock().withProperty("age", Integer.toString(++age)));
            }
        }
        // TODO; Specific cases for pumpkins/melons
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
