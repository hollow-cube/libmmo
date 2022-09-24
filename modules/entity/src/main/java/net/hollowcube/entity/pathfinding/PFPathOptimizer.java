package net.hollowcube.entity.pathfinding;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface PFPathOptimizer {

    @NotNull PFPath optimize(@NotNull PFPath path, @NotNull Instance instance, @NotNull BoundingBox bb);

    PFPathOptimizer NOOP = (path, instance, bb) -> path;

    PFPathOptimizer IAM = (path, instance, bb) -> {
        List<Point> newNodes = new ArrayList<>();

        {
            var nodes = path.nodes;
            int current = 0;
            int next = 1;

            for (int i = 0; i < nodes.size() - 1; i++) {
                var currentNode = nodes.get(current);
                var nextNode = nodes.get(next);
                var diff = nextNode.sub(currentNode);
                PhysicsResult res = CollisionUtils.handlePhysics(instance, instance.getChunkAt(currentNode), bb, Pos.fromPoint(currentNode), Vec.fromPoint(diff), null);
                if (res.collisionX() || res.collisionY() || res.collisionZ()) {
                    newNodes.add(nodes.get(next - 1));
                    newNodes.add(nodes.get(next));
                    current = next;
                }
                next++;
            }
            if (nodes.size() > 0)
                newNodes.add(nodes.get(nodes.size() - 1));

            path = new PFPath(newNodes);
        }

        {
            var nodes = path.nodes;
            newNodes = new ArrayList<>();
            for (int current = 0; current < nodes.size() - 1; current++) {
                var currentNode = nodes.get(current);
                var nextNode = nodes.get(current + 1);
                newNodes.add(currentNode);
                Point diff = nextNode.sub(currentNode);
                PhysicsResult res = CollisionUtils.handlePhysics(instance, instance.getChunkAt(currentNode), bb, Pos.fromPoint(currentNode), Vec.fromPoint(diff), null);
                if (res.collisionX() || res.collisionY() || res.collisionZ()) {
                    Point end = res.newPosition();
                    if (nextNode.distance(end) > 0.1) {
                        newNodes.add(end);
                    }
                }
            }
            if (nodes.size() > 0)
                newNodes.add(nodes.get(nodes.size() - 1));

            path = new PFPath(nodes);
        }

        return path;
    };

}
