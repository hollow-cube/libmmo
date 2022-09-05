package unnamed.mmo.entity.pathfinding;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;

import java.util.*;

public class PFPathGenerator {
    public static double heuristic (Point node, Point target) {
        return node.distance(target);
    }

    static Comparator<PFNode> pNodeComparator = (a, b) -> Float.compare(a.cost(), b.cost());
    public static PFPath generate(Instance instance, Point orgStart, Point orgTarget, double maxDistance, double closeDistance, BoundingBox boundingBox, PFPathOptimizer optimizer) {
        Point start = PFNode.gravitySnap(instance, orgStart);
        Point target = PFNode.gravitySnap(instance, orgTarget);
        if (start == null || target == null) return null;

        BoundingBox expandedBoundingBox = boundingBox.expand(0.5, 0, 0.5);
        PFNeighborSupplier neighborSupplier = new PFNeighborSupplier.Iam();

        int maxSize = (int) Math.floor(maxDistance * 7);

        PFNode startNode = new PFNode(start, 0);

        Queue<PFNode> open = new PriorityQueue<>(pNodeComparator);
        open.add(startNode);

        Map<PFNode, PFNode> cameFrom = new HashMap<>();

        Map<Point, Float> minCost = new HashMap<>();
        minCost.put(start, 0f);

        while (!open.isEmpty()) {
            PFNode current = open.peek();
            if (current.point().distance(target) < closeDistance)
                break; // Path is found

            if (cameFrom.size() >= maxSize) // Max search depth reached
                throw new RuntimeException("Ran out of nodes");

            open.poll();
            for (Point neighbor : neighborSupplier.getNeighbors(instance, current.point(), target, expandedBoundingBox)) {
                float neighborCost = (float) (minCost.get(current.point()) + current.point().distance(neighbor));
                if (neighborCost < minCost.getOrDefault(neighbor, Float.POSITIVE_INFINITY)) {
                    // New cheapest path, update indices
                    float neighborTotalCost = (float) (neighborCost + heuristic(neighbor, target));
                    PFNode neighborNode = new PFNode(neighbor, neighborTotalCost);
                    cameFrom.put(neighborNode, current);
                    minCost.put(neighbor, neighborCost);

                    if (open.contains(neighborNode)) {
                        open.remove(neighborNode);
                    }
                    open.offer(neighborNode);
                }

            }
//            var nearbyPoints = current.getNearby(instance, closed, value, expandedBoundingBox)
//                    .stream().filter(p -> p.point.distance(value) <= maxDistance).collect(Collectors.toSet());

        }

        List<Point> result = new ArrayList<>();
        PFNode current = open.peek();
        if (current == null) return null;
        result.add(current.point());
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            result.add(0, current.point());
        }

        return optimizer.optimize(new PFPath(result), instance, expandedBoundingBox);
    }

}
