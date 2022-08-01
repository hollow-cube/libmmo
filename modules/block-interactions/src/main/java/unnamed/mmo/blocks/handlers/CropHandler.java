package unnamed.mmo.blocks.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CropHandler implements BlockHandler {
    @Override
    public void onPlace(@NotNull Placement placement) {
        BlockHandler.super.onPlace(placement);
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        BlockHandler.super.onDestroy(destroy);
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        return BlockHandler.super.onInteract(interaction);
    }

    @Override
    public void onTouch(@NotNull Touch touch) {
        BlockHandler.super.onTouch(touch);
    }

    @Override
    public void tick(@NotNull Tick tick) {
        BlockHandler.super.tick(tick);
    }

    @Override
    public boolean isTickable() {
        return BlockHandler.super.isTickable();
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return BlockHandler.super.getBlockEntityTags();
    }

    @Override
    public byte getBlockEntityAction() {
        return BlockHandler.super.getBlockEntityAction();
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return null;
    }
}
