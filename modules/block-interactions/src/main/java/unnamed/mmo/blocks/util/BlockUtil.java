package unnamed.mmo.blocks.util;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockUtil {
    public static boolean isWater(@NotNull Block block) {
        if (block.id() == Block.WATER.id())
            return true;
        return "true".equals(block.getProperty("waterlogged"));
    }
}
