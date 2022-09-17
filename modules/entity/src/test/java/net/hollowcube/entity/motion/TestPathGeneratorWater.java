package net.hollowcube.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import net.hollowcube.test.MockBlockGetter;

import static com.google.common.truth.Truth.assertThat;

public class TestPathGeneratorWater {

    @Test
    public void testSingleWaterBlock() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.block(0, 0, 0, Block.WATER);
        var start = new Vec(0, 0, 0);

        var result = PathGenerator.WATER.generate(world, start, bb);
        assertThat(result).isEmpty();
    }

    @Test
    public void testSingleNeighbor() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.range(
                0, 0, 0,
                1, 0, 0,
                Block.WATER
        );
        var start = new Vec(0, 0, 0);

        var result = PathGenerator.WATER.generate(world, start, bb);
        assertThat(result).containsExactly(new Vec(1.5, 0, 0.5));
    }

    @Test
    public void testAllNeighbors() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.range(
                -1, -1, -1,
                1, 1, 1,
                Block.WATER
        );
        var start = new Vec(0, 0, 0);

        var result = PathGenerator.WATER.generate(world, start, bb);
        assertThat(result).containsExactly(
                new Vec(-0.5, 0, 0.5),
                new Vec(1.5, 0, 0.5),
                new Vec(0.5, -1, 0.5),
                new Vec(0.5, 1, 0.5),
                new Vec(0.5, 0, -0.5),
                new Vec(0.5, 0, 1.5)
        );
    }

    @Test
    public void testSingleNeighbor2() {
        var bb = new BoundingBox(0.5, 0.5, 0.5);
        var world = MockBlockGetter.range(
                -2, -2, -2,
                2, 2, 2,
                Block.STONE
        ).set(0, 0, 0, Block.WATER).set(1, 0, 0, Block.WATER);
        var start = new Vec(0.5, 0, 0.5);

        var result = PathGenerator.WATER.generate(world, start, bb);
        assertThat(result).containsExactly(new Vec(1.5, 0, 0.5));
    }

    @Test
    public void testSingleNeighborBigBB() {
        // Single neighbor surrounded by stone, but the BB is too big to fit
        var bb = new BoundingBox(1.5, 1.5, 1.5);
        var world = MockBlockGetter.range(
                -2, -2, -2,
                2, 2, 2,
                Block.STONE
        ).set(0, 0, 0, Block.WATER).set(1, 0, 0, Block.WATER);
        var start = new Vec(0.5, 0, 0.5);

        var result = PathGenerator.WATER.generate(world, start, bb);
        assertThat(result).isEmpty();
    }

}
