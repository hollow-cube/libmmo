package net.hollowcube.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import net.hollowcube.test.MockBlockGetter;

import static com.google.common.truth.Truth.assertThat;

public class TestPathGeneratorLand {

    @Test
    public void testEmpty() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var start = new Vec(0, 1, 0);

        var result = PathGenerator.LAND.generate(world, start, bb);
        assertThat(result).isEmpty();
    }

    @Test
    public void testSingleNeighbor() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.range(
                0, 0, 0,
                1, 0, 0,
                Block.STONE);
        var start = new Vec(0, 1, 0);

        var result = PathGenerator.LAND.generate(world, start, bb);
        assertThat(result).containsExactly(
                new Vec(1.5, 1, 0.5)
        );
    }

    @Test
    public void testAllNeighborsFlat() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.range(
                -1, 0, -1,
                1, 0, 1,
                Block.STONE);
        var start = new Vec(0, 1, 0);

        var result = PathGenerator.LAND.generate(world, start, bb);
        assertThat(result).containsExactly(
                new Vec(1.5, 1, 0.5),
                new Vec(-0.5, 1, 0.5),
                new Vec(0.5, 1, 1.5),
                new Vec(0.5, 1, -0.5)
        );
    }

    @Test
    public void testSingleNeighborBigBB() {
        var bb = new BoundingBox(0.6, 1.95, 0.6);
        var world = MockBlockGetter.range(
                0, 0, 0,
                1, 0, 0,
                Block.STONE)
                .set(1, 2, 0, Block.STONE);
        var start = new Vec(0, 1, 0);

        var result = PathGenerator.LAND.generate(world, start, bb);
        assertThat(result).isEmpty();
    }

    @Test
    public void testSingleStepUp() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty()
                .set(1, 1, 0, Block.STONE);
        var start = new Vec(0, 1, 0);

        var result = PathGenerator.LAND.generate(world, start, bb);
        assertThat(result).containsExactly(
                new Vec(1.5, 2, 0.5)
        );
    }

    @Test
    public void testSingleStepDown() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty()
                .set(1, -1, 0, Block.STONE);
        var start = new Vec(0, 1, 0);

        var result = PathGenerator.LAND.generate(world, start, bb);
        assertThat(result).containsExactly(
                new Vec(1.5, 0, 0.5)
        );
    }

}
