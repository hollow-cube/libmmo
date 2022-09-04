package unnamed.mmo.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.motion.util.PhysicsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generates paths to be consumed by a {@link Pathfinder}.
 * <p>
 * Should take into account the entity capabilities, e.g. avoiding danger.
 */
public interface PathGenerator {

    @NotNull Collection<Point> generate(@NotNull Block.Getter world, @NotNull Point pos, @NotNull BoundingBox bb);


    PathGenerator LAND = (world, pos, bb) -> {

        return List.of();
    };

    PathGenerator WATER = (world, pos, bb) -> {
        List<Point> neighbors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            var neighbor = pos.add(direction.normalX(), direction.normalY(), direction.normalZ());
            // Ensure the block is water, otherwise we cannot move to it
            if (world.getBlock(neighbor, Block.Getter.Condition.TYPE).id() != Block.WATER.id()) continue;
            // Ensure the BB fits at that block
            if (PhysicsUtil.testCollision(world, neighbor, bb)) continue;

            neighbors.add(neighbor);
        }
        return neighbors;
    };

    PathGenerator AIR = (world, pos, bb) -> {

        return List.of();
    };

}
