package net.hollowcube.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.junit.jupiter.api.Test;
import net.hollowcube.test.MockBlockGetter;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static net.minestom.server.instance.block.Block.Getter.Condition;

public class TestPathfinderAStar {

    @Test
    public void testSamePoint() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var start = new Vec(0, 0, 0);
        var goal = new Vec(0, 0, 0);

        var result = Pathfinder.A_STAR.findPath(ALL, world, start, goal, bb);
        assertThat(result).isNotNull();
        assertThat(result.nodes()).containsExactly(new Vec(0, 0, 0));
    }

    @Test
    public void testBasicLine() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var start = new Vec(0.5, 0, 0.5);
        var goal = new Vec(3, 0, 0);

        var result = Pathfinder.A_STAR.findPath(ALL, world, start, goal, bb);
        assertThat(result).isNotNull();
        assertThat(result.nodes()).containsExactly(
                new Vec(0.5, 0, 0.5),
                new Vec(1.5, 0, 0.5),
                new Vec(2.5, 0, 0.5)
        );
    }

    @Test
    public void testBasicAvoidance() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.block(1, 0, 0, Block.STONE);
        var start = new Vec(0.5, 0, 0.5);
        var goal = new Vec(2.5, 0, 0.5);

        var result = Pathfinder.A_STAR.findPath(ALL, world, start, goal, bb);
        assertThat(result).isNotNull();
        assertThat(result.nodes()).containsExactly(
                new Vec(0.5, 0, 0.5),
                new Vec(0.5, 0, 1.5),
                new Vec(1.5, 0, 1.5),
                new Vec(2.5, 0, 1.5),
                new Vec(2.5, 0, 0.5)
        );
    }


    // A path generator which returns any solid block in a direction (up/down/nsew)
    private static final PathGenerator ALL = (world, pos, bb) -> {
        pos = new Vec(pos.blockX() + 0.5, pos.blockY(), pos.blockZ() + 0.5);
        List<Point> neighbors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            var neighbor = pos.add(direction.normalX(), direction.normalY(), direction.normalZ());
            if (world.getBlock(neighbor, Condition.TYPE).isSolid()) continue;
            neighbors.add(neighbor);
        }
        return neighbors;
    };

}
