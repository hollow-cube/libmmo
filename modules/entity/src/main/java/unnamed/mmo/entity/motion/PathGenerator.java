package unnamed.mmo.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.motion.util.PhysicsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minestom.server.instance.block.Block.Getter.Condition;

/**
 * Generates paths to be consumed by a {@link Pathfinder}.
 * <p>
 * Should take into account the entity capabilities, e.g. avoiding danger.
 */
public interface PathGenerator {

    @NotNull Collection<Point> generate(@NotNull Block.Getter world, @NotNull Point pos, @NotNull BoundingBox bb);


    PathGenerator LAND = (world, pos, bb) -> {
        pos = new Vec(pos.blockX() + 0.5, pos.blockY(), pos.blockZ() + 0.5);
        List<Point> neighbors = new ArrayList<>();
        for (Direction direction : Direction.HORIZONTAL) {
            for (int y = -1; y <= 1; y++) {
                var neighbor = pos.add(direction.normalX(), direction.normalY() + y, direction.normalZ());
                // Block below must be solid, or we cannot move to it
                if (!world.getBlock(neighbor.add(0, -1, 0), Condition.TYPE).isSolid()) continue;
                // Ensure the BB fits at that block
                if (PhysicsUtil.testCollision(world, neighbor, bb)) continue;

                neighbors.add(neighbor);
            }
        }
        return neighbors;
    };

    PathGenerator WATER = (world, pos, bb) -> {
        pos = new Vec(pos.blockX() + 0.5, pos.blockY(), pos.blockZ() + 0.5);
        List<Point> neighbors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            var neighbor = pos.add(direction.normalX(), direction.normalY(), direction.normalZ());
            // Ensure the block is water, otherwise we cannot move to it
            if (world.getBlock(neighbor, Condition.TYPE).id() != Block.WATER.id()) continue;
            // Ensure the BB fits at that block
            if (PhysicsUtil.testCollision(world, neighbor, bb)) continue;

            neighbors.add(neighbor);
        }
        return neighbors;
    };

    PathGenerator AIR = (world, pos, bb) -> {
        //todo
        return List.of();
    };

}
