package unnamed.mmo.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.motion.util.PhysicsUtil;

import java.util.ArrayList;
import java.util.List;

public interface PathOptimizer {

    @NotNull Path optimize(@NotNull Path path, @NotNull Block.Getter world, @NotNull BoundingBox bb);


    /** Returns the input path with no modification. */
    PathOptimizer NOOP = (path, world, bb) -> path;

    /**
     * Walks the path attempting to drop intermediate nodes and walk directly to the next one.
     * If there is collision, add the current node and start again on the next one.
     */
    PathOptimizer STRING_PULL = (path, world, bb) -> {
        // On short paths there is nothing that can be shortened.
        if (path.size() < 3) return path;

        List<Point> newPath = new ArrayList<>();
        newPath.add(path.get(0));

        int current = 0;
        int next = 1;
        for (int i = 0; i < path.size() - 1; i++) {
            boolean didCollide = PhysicsUtil.testCollisionSwept(world, bb, path.get(current), path.get(next));
            if (didCollide) {
                newPath.add(path.get(next - 1));
                current = next;
            }
            next++;
        }

        newPath.add(path.get(path.size() - 1));
        return new Path(newPath);
    };

}
