package unnamed.mmo.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.entity.motion.util.PhysicsUtil;

import java.util.*;

/**
 * Consumes nodes from a {@link PathGenerator} to create a path.
 */
public interface Pathfinder {

    @Nullable Path findPath(
            @NotNull PathGenerator pathGenerator,
            @NotNull Block.Getter world,
            @NotNull Point from,
            @NotNull Point to,
            @NotNull BoundingBox bb
    );

    /**
     * Reference implementation of a*
     *
     * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">wikipedia</a>
     */
    Pathfinder A_STAR = (pathGenerator, world, start, goal, bb) -> {
        //todo should only snap for land, otherwise it should target directly. perhaps the path generator will
        // have to have a method to prep the start and end or something
//        final Point start = PhysicsUtil.gravitySnap(world, from);
//        final Point goal = PhysicsUtil.gravitySnap(world, to);
//        if (start == null || goal == null) return null;

        // Min acceptable distance from the target.
        //todo this should be a parameter somewhere i guess
        // Also i am not sure this is necessary? could keep this internal and just add the target as the final point?
        float minDistance = 0.8f;

        //todo should the bb be expanded?

        //todo maxSize
        int maxSize = 100 * 7;

        Map<Point, Float> minCost = new HashMap<>();
        minCost.put(start, 0f);

        //todo probably super slot to do a lookup for each of these
        Queue<Point> open = new PriorityQueue<>((a, b) -> Float.compare(minCost.get(a), minCost.get(b)));
        open.add(start);

        Map<Point, Point> cameFrom = new HashMap<>();


        while (!open.isEmpty()) {
            Point current = open.peek();
            if (current.distance(goal) < minDistance)
                break; // Path was found

            // Safety stop if we are never actually going to find it
            if (cameFrom.size() >= maxSize) {
                //todo log
                return null;
            }

            open.poll();
            float currentCost = minCost.get(current);
            for (var neighbor : pathGenerator.generate(world, current, bb)) {
                float neighborCost = (float) (currentCost + current.distance(neighbor));
                if (neighborCost < minCost.getOrDefault(neighbor, Float.POSITIVE_INFINITY)) {
                    // New cheapest path, update indices
                    float neighborTotalCost = (float) (neighborCost + neighbor.distance(goal)); // Heuristic is just distance
                    cameFrom.put(neighbor, current);
                    minCost.put(neighbor, neighborTotalCost);

                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }
        }

        // Backtrack using cameFrom to find the best path
        List<Point> result = new ArrayList<>();
        Point current = open.peek();
        if (current == null) return null; // Ran out of nodes
        result.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            result.add(0, current);
        }

        //todo optimize the path

        return new Path(result);
    };
}
