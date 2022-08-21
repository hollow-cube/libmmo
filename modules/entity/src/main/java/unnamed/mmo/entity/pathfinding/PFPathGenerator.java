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

    private static Collection<Point> getNeighborPositions(Point point) {
        return List.of(
                point.add(1, 0, 0),
                point.add(-1, 0, 0),
                point.add(0, 0, 1),
                point.add(0, 0, -1)
        );
    }

    static Comparator<PFNode> pNodeComparator = (a, b) -> Float.compare(a.cost(), b.cost());
    public static PFPath generate(Instance instance, Point orgStart, Point orgTarget, double maxDistance, double closeDistance, BoundingBox boundingBox) {
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
//            var nearbyPoints = current.getNearby(instance, closed, target, expandedBoundingBox)
//                    .stream().filter(p -> p.point.distance(target) <= maxDistance).collect(Collectors.toSet());

        }

        List<Point> result = new ArrayList<>();
        PFNode current = open.peek();
        if (current == null) return null;
        result.add(current.point());
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            result.add(0, current.point());
        }

//        Collections.reverse(result);
//        if (open.isEmpty()) return null;
//
//        PNode current = open.poll();
//
//        if (current.point.distance(target) > closeDistance) return null;
//
//        while (current.parent != null) {
//            path.getNodes().add(current);
//            current = current.parent;
//        }
//
//        Collections.reverse(path.getNodes());
//        path.reduceNodes(instance, expandedBoundingBox);
//        path.addNodes(instance, expandedBoundingBox);

//        if (path.getNodes().size() > 0) {
//            PNode pEnd = new PNode(target, 0, 0, path.getNodes().get(path.getNodes().size() - 1));
//            path.getNodes().add(pEnd);
//        }
//
//        return path;
        return new PFPath(result);
    }

    public static void main(String[] args) {
        Queue<PFNode> queue = new PriorityQueue<>(pNodeComparator);
        PFNode a = new PFNode(Vec.ZERO, 2);
        queue.offer(a);
        queue.offer(a);
        queue.remove(a);

        PFPathGenerator.generate(null, new Vec(0, 0, 0), new Vec(5, 0, 0), 10, 0.8, null);
    }
}
