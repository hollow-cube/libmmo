package unnamed.mmo.blocks.util;

import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

public class TestBlockUtil {

    private static Stream<Arguments> isWaterCases() {
        return Stream.of(
                Arguments.of(Block.WATER, true),
                Arguments.of(Block.fromStateId((short) (Block.WATER.stateId() + 1)), true),
                Arguments.of(Block.OAK_STAIRS.withProperty("waterlogged", "true"), true),
                Arguments.of(Block.OAK_STAIRS, false),
                Arguments.of(Block.AIR, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isWaterCases")
    public void testIsWater(Block block, boolean expected) {
        var result = BlockUtil.isWater(block);
        assertThat(result).isEqualTo(expected);
    }
}
