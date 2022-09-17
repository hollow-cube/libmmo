package net.hollowcube.entity.motion;

import net.hollowcube.entity.brain.navigator.Navigator;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Consumes paths from a {@link Pathfinder} to navigate an {@link Entity} in an instance.
 */
public final class MotionNavigatorSlime implements Navigator {
    private final Cooldown jumpCooldown = new Cooldown(Duration.of(40, TimeUnit.SERVER_TICK));
    private final Entity entity;

    private Point goal = null;
    private Path path = null;
    private int index = 0;

    public MotionNavigatorSlime(@NotNull Entity entity) {
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
            return;
        }

        Point current = index < path.size() ? path.get(index) : goal;

        float movementSpeed = 0.1f;
        if (entity instanceof LivingEntity livingEntity) {
            movementSpeed = livingEntity.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue();
        }

        // Alternative way to do this movement:
        // - rotate towards the target pos each tick (interpolated probably, though the client interpolation might be enough)
        // - jump in facing direction occasionally

        // Move towards the current target, trying to jump if stuck
        if (jumpCooldown.isReady(time)) {
            moveTowards(current, movementSpeed);
            jumpCooldown.refreshLastUpdate(time);
        }

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
        final double speedX = Math.cos(radians) * speed * 10;
        final double speedY = 8 + dy * speed * 2.5;
        final double speedZ = Math.sin(radians) * speed * 10;
        entity.setVelocity(new Vec(speedX, speedY, speedZ));
        return true;

//        final float yaw = PositionUtils.getLookYaw(dx, dz);
//        final float pitch = PositionUtils.getLookPitch(dx, dy, dz);
//        // Prevent ghosting
//        final var physicsResult = CollisionUtils.handlePhysics(entity, new Vec(speedX, speedY, speedZ));
//        this.entity.refreshPosition(Pos.fromPoint(physicsResult.newPosition()).withView(yaw, pitch));
//        return physicsResult.collisionX() || physicsResult.collisionY() || physicsResult.collisionZ();
    }

}
