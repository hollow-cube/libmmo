package net.hollowcube.entity.motion.util;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import net.hollowcube.test.MockBlockGetter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestPhysicsUtil {

    public static class TestCollision {
        @Test
        public void testEmpty() {
            var bb = new BoundingBox(100, 100, 100);
            var world = MockBlockGetter.empty();
            var pos = new Vec(0, 0, 0);

            var result = PhysicsUtil.testCollision(world, pos, bb);

            assertFalse(result);
        }

        @Test
        public void testSmallInsideBlock() {
            var bb = new BoundingBox(0.5, 0.5, 0.5);
            var world = MockBlockGetter.block(0, 0, 0, Block.STONE);
            var pos = new Vec(0, 0, 0);

            var result = PhysicsUtil.testCollision(world, pos, bb);

            assertTrue(result);
        }

        @Test
        public void testBetweenBlocks() {
            var bb = new BoundingBox(0.6, 1.95, 0.6);
            var world = MockBlockGetter.block(1, 1, 0, Block.STONE).set(-1, 1, 0, Block.STONE);
            var pos = new Vec(0, 0, 0);

            var result = PhysicsUtil.testCollision(world, pos, bb);

            assertTrue(result);
        }

        @Test
        public void testUnderBlock() {
            var bb = new BoundingBox(0.6, 1.95, 0.6);
            var world = MockBlockGetter.block(0, 1, 0, Block.STONE);
            var pos = new Vec(0.5, 0, 0.5);

            var result = PhysicsUtil.testCollision(world, pos, bb);

            assertTrue(result);
        }

        @Test
        public void testUnderBlock2() {
            var bb = new BoundingBox(0.6, 1.95, 0.6);
            var world = MockBlockGetter.block(0, 2, 0, Block.STONE);
            var pos = new Vec(0.5, 0, 0.5);

            var result = PhysicsUtil.testCollision(world, pos, bb);

            assertFalse(result);
        }

        @Test
        public void testWaterNoCollision() {
            var bb = new BoundingBox(0.6, 0.6, 0.6);
            var world = MockBlockGetter.range(-1, -1, -1, 1, 1, 1, Block.WATER);
            var pos = new Vec(0.5, 0, 0.5);

            var result = PhysicsUtil.testCollision(world, pos, bb);

            assertFalse(result);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("gravitySnapCases")
    public void testGravitySnap(String name, Point start, boolean expected) {
        var world = MockBlockGetter.block(0, 50, 0, Block.STONE);
        var result = PhysicsUtil.gravitySnap(world, start);
        if (expected) {
            assertEquals(new Vec(0, 51, 0), result);
        } else {
            assertNull(result);
        }
    }

    private static Stream<Arguments> gravitySnapCases() {
        // Each case has a block at 0, 50, 0
        return Stream.of(
                Arguments.of("test correct place already", new Vec(0, 51, 0), true),
                Arguments.of("inside block", new Vec(0, 50, 0), true),
                Arguments.of("above block", new Vec(0, 58, 0), true),
                Arguments.of("below block (fail to find)", new Vec(0, 40, 0), false)
        );
    }

}
