package unnamed.mmo.entity.motion;

import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.Layer;
import com.mattworzala.debug.shape.Box;
import com.mattworzala.debug.shape.Line;
import com.mattworzala.debug.shape.OutlineBox;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.position.PositionUtils;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.entity.brain.navigator.Navigator;

import java.time.Duration;
import java.util.ArrayList;

/**
 * Consumes paths from a {@link Pathfinder} to navigate an {@link net.minestom.server.entity.Entity} in an instance.
 */
public final class MotionNavigator implements Navigator {
    private final Cooldown jumpCooldown = new Cooldown(Duration.of(40, TimeUnit.SERVER_TICK));
    private final Cooldown debugCooldown = new Cooldown(Duration.of(1, TimeUnit.SERVER_TICK));
    private final Entity entity;

    private Point goal = null;
    private Path path = null;
    private int index = 0;

    public MotionNavigator(@NotNull Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean isActive() {
        return path != null;
    }

    public void reset() {
        goal = null;
        path = null;
        index = 0;
    }

    @Override
    public synchronized boolean setPathTo(@Nullable Point point) {
        // Providing a null point clears the navigation task
        if (point == null) {
            reset();
            return true;
        }

        float minDistance = 0.8f; //todo move me

        // Early exit if trying to path to the same point as before, or the entity is already close enough
        if (goal != null && path != null && point.samePoint(goal))
            return true;
        if (entity.getPosition().distance(point) < minDistance) {
            // In this case we reset because we are changing the path to the given point (which we are
            // nearby already) so navigation should stop after this point.
            reset();
            return true;
        }

        // Ensure the entity is in an instance
        final Instance instance = entity.getInstance();
        if (instance == null)
            return false;

        // Cannot set a path outside the world border
        if (!instance.getWorldBorder().isInside(point))
            return false;

        // Cannot path to an unloaded chunk
        final Chunk chunk = instance.getChunkAt(point);
        if (chunk == null || !chunk.isLoaded())
            return false;

        // Attempt to find a path
        path = Pathfinder.A_STAR.findPath(PathGenerator.LAND, instance,
                entity.getPosition(), point, entity.getBoundingBox());

        boolean success = path != null;
        goal = success ? point : null;
        return success;
    }

    @Override
    public void tick(long time) {
        if (goal == null || path == null) return; // No path
        if (entity instanceof LivingEntity livingEntity && livingEntity.isDead()) return;

        // If we are close enough to the goal position, just stop
        float minDistance = 0.8f; //todo move me
        if (entity.getDistance(goal) < minDistance) {
            reset();
            sendDebugData();
            return;
        }

        if (debugCooldown.isReady(time)) {
            debugCooldown.refreshLastUpdate(time);
            sendDebugData();
        }

        Point current = index < path.size() ? path.get(index) : goal;

        float movementSpeed = 0.1f;
        if (entity instanceof LivingEntity livingEntity) {
            movementSpeed = livingEntity.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue();
        }

        // Move towards the current target, trying to jump if stuck
        boolean isStuck = moveTowards(current, movementSpeed);
        //todo jump if stuck

        // Move to next point if stuck
        if (entity.getPosition().distanceSquared(current) < 0.4) {
            index++;
        }
    }

    private boolean moveTowards(@NotNull Point direction, double speed) {
        final Pos position = entity.getPosition();
        final double dx = direction.x() - position.x();
        final double dy = direction.y() - position.y();
        final double dz = direction.z() - position.z();
        // the purpose of these few lines is to slow down entities when they reach their destination
        final double distSquared = dx * dx + dy * dy + dz * dz;
        if (speed > distSquared) {
            speed = distSquared;
        }
        final double radians = Math.atan2(dz, dx);
        final double speedX = Math.cos(radians) * speed;
        final double speedY = dy * speed;
        final double speedZ = Math.sin(radians) * speed;
        final float yaw = PositionUtils.getLookYaw(dx, dz);
        final float pitch = PositionUtils.getLookPitch(dx, dy, dz);

        // Prevent ghosting
        final var physicsResult = CollisionUtils.handlePhysics(entity, new Vec(speedX, speedY, speedZ));
        this.entity.refreshPosition(Pos.fromPoint(physicsResult.newPosition()).withView(yaw, pitch));
        return physicsResult.collisionX() || physicsResult.collisionY() || physicsResult.collisionZ();
    }


    // SECTION: Debug rendering
    // Eventually this should be only in the dev server. Just don't currently have a way to do a "mixin" here.
    // Probably will have some way to set the entity provider somewhere.

    private @NotNull String debugNamespace(){
        return "debug_" + entity.getUuid();
    }

    private void sendDebugData() {
        var builder = DebugMessage.builder()
                .clear(debugNamespace());

        addPathfinderDebugData(builder);
        addTargetPoint(builder);

        // Send the server side view

        builder.set(NamespaceID.from(debugNamespace(), "view_dir"), new Line.Builder()
                .point(entity.getPosition().asVec())
                .point(entity.getPosition().direction().mul(2).add(entity.getPosition().asVec()))
                .color(0xFFFFFFFF)
                .layer(Layer.TOP)
                .build());

        builder.build()
                .sendTo(entity.getViewersAsAudience());
    }

    private void addPathfinderDebugData(DebugMessage.Builder builder) {
        if (path == null) return;
        var nodes = path.nodes();
        var linePoints = new ArrayList<Vec>();

        for (int i = index; i < nodes.size(); i++) {
            var pos = Vec.fromPoint(nodes.get(i));
            builder.set(
                    debugNamespace() + ":pf_node_" + i,
                    new Box(pos.sub(0.4, 0.0, 0.4), pos.add(0.4, 0.1, 0.4), 0x331CB2F5, Layer.TOP)
            );
            linePoints.add(pos.withY(y -> y + 0.05));
        }
        builder.set(
                debugNamespace() + ":pf_path",
                new Line(linePoints, 10f, 0xFF1CB2F5, Layer.TOP)
        );
    }

    private void addTargetPoint(DebugMessage.Builder builder) {
        if (goal == null) return;
        builder.set(
                debugNamespace() + ":pf_target",
                new OutlineBox.Builder()
                        .block(goal.blockX(), goal.blockY(), goal.blockZ(), 0)
                        .color(0x55FF0000)
                        .layer(Layer.TOP)
                        .colorLine(0xFFFF0000)
                        .layerLine(Layer.TOP)
                        .build()
        );
    }

}
