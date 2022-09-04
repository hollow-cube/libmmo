package unnamed.mmo.entity.motion.util;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import unnamed.mmo.test.MockBlockGetter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPhysicsUtil {

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
        var world = MockBlockGetter.block(1, 1, 0, Block.STONE)
                .set(-1, 1, 0, Block.STONE);
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
