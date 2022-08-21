package unnamed.mmo.entity.pathfinding;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public interface PFNeighborSupplier {

    //todo right now supplier cannot influence cost, that is probably not desirable behavior
    @NotNull
    Collection<@NotNull Point> getNeighbors(Block.Getter blockGetter, Point current, Point goal, @NotNull BoundingBox expandedBoundingBox);

    class Iam implements PFNeighborSupplier {

        @Override
        public @NotNull Collection<@NotNull Point> getNeighbors(Block.Getter blockGetter, Point current, Point goal, @NotNull BoundingBox expandedBoundingBox) {
            Collection<Point> nearby = new ArrayList<>();

            int width = (int) Math.ceil(expandedBoundingBox.width() / 2);

            for (int x = -width; x <= width; x++) {
                for (int z = -width; z <= width; z++) {
                    if (x == 0 && z == 0) continue;

                    Point orgPoint = new Vec(current.blockX(), current.blockY(), current.blockZ()).add(x, 0, z).add(0.5, 0, 0.5);
                    Point point = PFNode.gravitySnap(blockGetter, orgPoint);
//                    Point point = orgPoint;

                    if (point == null) continue;

                    //todo may be worth adding something like this back later (more expensive to jump)
//                    float cost = orgPoint.blockY() == point.blockY() ? 0.9f : 4.5f;
//                    if (x != 0 && z != 0) cost -= 0.2;

                    if (blockGetter.getBlock(point, Block.Getter.Condition.TYPE).isSolid()) continue;
                    if (!blockGetter.getBlock(point.sub(0, 1, 0), Block.Getter.Condition.TYPE).isSolid()) continue;

                    //todo this stops my villager from finding a path between two blocks
//                    Collection<Point> overlapping = BoundingBoxUtilKt.getBlocks(expandedBoundingBox, point);
//
//                    boolean isInvalid = false;
//                    for (Point block : overlapping) {
//                        if (blockGetter.getBlock(block, Block.Getter.Condition.TYPE).isSolid()) {
//                            isInvalid = true;
//                            break;
//                        }
//                    }
//
//                    if (isInvalid) {
//                        // Check up 1 block
//                        boolean isInvalidUp = false;
//                        for (Point block : overlapping) {
//                            if (blockGetter.getBlock(block.add(0, 1, 0), Block.Getter.Condition.TYPE).isSolid()) {
//                                isInvalidUp = true;
//                                break;
//                            }
//                        }
//
//                        if (isInvalidUp) continue;
//                        point = point.add(0, 1, 0);
//                    }

                    //todo a slight optimization would be to not add any visited points to the list
                    nearby.add(point);
                }
            }

            return nearby;
        }
    }
}
