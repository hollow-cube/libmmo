package unnamed.mmo.entity.motion.util;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.minestom.server.instance.block.Block.Getter.Condition;

/**
 * Amalgamation of Minestom physics utility calls and a simpler static bounding box check in a world.
 */
@SuppressWarnings("UnstableApiUsage")
public final class PhysicsUtil {
    private PhysicsUtil() {}

    private static final int MAX_SNAP_DISTANCE = 32;

    private static final MethodHandle handlePhysics;


    /**
     * Simplified check if a bounding box collides with a solid block.
     * <p>
     * Currently, block shapes are ignored. Solid=full cube, empty=nothing
     */
    public static boolean testCollision(@NotNull Block.Getter world, @NotNull Point pos, @NotNull BoundingBox bb) {
        List<Point> blocks = new ArrayList<>();
        for (double x = bb.minX() + pos.x(); x <= bb.maxX() + pos.x(); x++) {
            for (double y = bb.minY() + pos.y(); y <= bb.maxY() + pos.y(); y++) {
                for (double z = bb.minZ() + pos.z(); z <= bb.maxZ() + pos.z(); z++) {
                    blocks.add(new Vec(Math.floor(x), Math.floor(y), Math.floor(z)));
                }
                blocks.add(new Vec(Math.floor(x), Math.floor(y), Math.floor(bb.maxX() + pos.z())));
            }
            blocks.add(new Vec(Math.floor(x), Math.floor(bb.maxY() + pos.y()), Math.floor(bb.minZ() + pos.z())));
        }
        blocks.add(new Vec(Math.floor(bb.maxX() + pos.x()), Math.floor(bb.maxY() + pos.y()), Math.floor(bb.maxZ() + pos.z())));

        for (var block : blocks) {
            if (world.getBlock(block, Block.Getter.Condition.TYPE).isSolid()) {
                return true;
            }
        }
        return false;

//        if (isInvalid) {
//            boolean isInvalidUp = false;
//            for (var block : blocks) {
//                if (world.getBlock(block.add(0, 1, 0), Block.Getter.Condition.TYPE).isSolid()) {
//                    isInvalidUp = true;
//                    break;
//                }
//            }
//
//            if (isInvalidUp) return true;
//        }
        //Collection<Point> overlapping = BoundingBoxUtilKt.getBlocks(expandedBoundingBox, point);
//
//         boolean isInvalid = false;
//         for (Point block : overlapping) {
//             if (blockGetter.getBlock(block, Block.Getter.Condition.TYPE).isSolid()) {
//                 isInvalid = true;
//                 break;
//             }
//         }
//
//         if (isInvalid) {
//             // Check up 1 block
//             boolean isInvalidUp = false;
//             for (Point block : overlapping) {
//                 if (blockGetter.getBlock(block.add(0, 1, 0), Block.Getter.Condition.TYPE).isSolid()) {
//                     isInvalidUp = true;
//                     break;
//                 }
//             }
//
//             if (isInvalidUp) continue;
//             point = point.add(0, 1, 0);
//         }
    }

    /**
     * Collision check from a starting point to a given position.
     * <p>
     * Uses Minestom internal physics check, which does account for block shapes.
     */
    public static boolean testCollisionSwept(@NotNull Block.Getter world, @NotNull BoundingBox bb, @NotNull Point from, @NotNull Point to) {
        PhysicsResult result;
        if (world instanceof Instance instance) {
            result = CollisionUtils.handlePhysics(instance, instance.getChunkAt(from),
                    bb, Pos.fromPoint(from), Vec.fromPoint(to.sub(from)), null);
        } else {
            // Not advisable in production, but acceptable to use in tests
            try {
                result = (PhysicsResult) handlePhysics.invoke(bb, Vec.fromPoint(to.sub(from)), Pos.fromPoint(from), world, null);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        return result.collisionX() || result.collisionY() || result.collisionZ();
    }

    /**
     * Snap the given point to the ground. If the point is the ground block, this moves it to
     * the air block on top of the ground, if it is in the air, it snaps to the ground underneath.
     * <p>
     * If there is no block within {@link #MAX_SNAP_DISTANCE}, null is returned.
     */
    public static @Nullable Point gravitySnap(@NotNull Block.Getter world, @NotNull Point point) {
        if (world.getBlock(point, Condition.TYPE).isSolid())
            return point.add(0, 1, 0);

        var ground = point.sub(0, 1, 0);
        while (!world.getBlock(ground, Condition.TYPE).isSolid()) {
            ground = ground.sub(0, 1, 0);
            if (Math.abs(ground.blockY() - point.blockY()) > MAX_SNAP_DISTANCE)
                return null;
        }

        // Snap to the exact Y position
        //todo need to take into account bounding box
        return ground.withY(y -> Math.floor(y + 1));
    }

    static {
        try {
            Class<?> blockCollision = Class.forName("net.minestom.server.collision.BlockCollision");
            Method rHandlePhysics = blockCollision.getDeclaredMethod("handlePhysics", BoundingBox.class, Vec.class, Pos.class, Block.Getter.class, PhysicsResult.class);
            rHandlePhysics.setAccessible(true);
            handlePhysics = MethodHandles.lookup().unreflect(rHandlePhysics);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
