package unnamed.mmo.util;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class BlockUtil {

    public static @NotNull Block withType(@NotNull Block block, @NotNull Block type) {
        return type.withHandler(block.handler()).withNbt(block.nbt());
    }

}
