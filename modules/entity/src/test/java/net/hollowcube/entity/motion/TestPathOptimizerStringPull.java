package net.hollowcube.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import net.hollowcube.test.MockBlockGetter;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestPathOptimizerStringPull {

    @Test
    public void testEmpty() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var path = new Path(List.of());

        var result = PathOptimizer.STRING_PULL.optimize(path, world, bb);
        assertThat(result.nodes()).isEmpty();
    }

    @Test
    public void testTwoPoints() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var path = new Path(List.of(
                new Vec(0, 0, 0),
                new Vec(1, 0, 0)
        ));

        var result = PathOptimizer.STRING_PULL.optimize(path, world, bb);
        assertThat(result.nodes()).isEqualTo(path.nodes());
    }

    @Test
    public void testThreePointsNoObstacles() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var path = new Path(List.of(
                new Vec(0, 0, 0),
                new Vec(1, 0, 0),
                new Vec(1, 0, 1)
        ));

        var result = PathOptimizer.STRING_PULL.optimize(path, world, bb);
        assertThat(result.nodes()).containsExactly(
                new Vec(0, 0, 0),
                new Vec(1, 0, 1)
        );
    }

    @Test
    public void testFivePointsNoObstacles() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.empty();
        var path = new Path(List.of(
                new Vec(0, 0, 0),
                new Vec(1, 0, 0),
                new Vec(1, 0, 1),
                new Vec(1, 0, 2),
                new Vec(1, 0, 3)
        ));

        var result = PathOptimizer.STRING_PULL.optimize(path, world, bb);
        assertThat(result.nodes()).containsExactly(
                new Vec(0, 0, 0),
                new Vec(1, 0, 3)
        );
    }

    @Test
    public void testThreePointsWithObstacle() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.block(1, 0, 1, Block.STONE);
        var path = new Path(List.of(
                new Vec(0, 0, 0),
                new Vec(2, 0, 0),
                new Vec(2, 0, 2)
        ));

        var result = PathOptimizer.STRING_PULL.optimize(path, world, bb);
        assertThat(result.nodes()).containsExactly(
                new Vec(0, 0, 0),
                new Vec(2, 0, 0),
                new Vec(2, 0, 2)
        );
    }

    @Test
    public void testFivePointsWithObstacle() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.block(1, 0, 1, Block.STONE);
        var path = new Path(List.of(
                new Vec(0, 0, 0),
                new Vec(2, 0, 0),
                new Vec(2, 0, 2),
                new Vec(2, 0, 4),
                new Vec(2, 0, 6)
        ));

        var result = PathOptimizer.STRING_PULL.optimize(path, world, bb);
        assertThat(result.nodes()).containsExactly(
                new Vec(0, 0, 0),
                new Vec(2, 0, 0),
                new Vec(2, 0, 6)
        );
    }
}
