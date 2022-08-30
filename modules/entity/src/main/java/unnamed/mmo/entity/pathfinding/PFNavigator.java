package unnamed.mmo.entity.pathfinding;

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
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.utils.position.PositionUtils;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;

// TODO all pathfinding requests could be processed in another thread

public final class PFNavigator {
    private Point goalPosition;
    private final Entity entity;
    private PFPath path;
    private final Cooldown jumpCooldown = new Cooldown(Duration.of(40, TimeUnit.SERVER_TICK));
    private double minimumDistance;

    public PFNavigator(@NotNull Entity entity) {
        this.entity = entity;
    }

    /**
     * Used to move the entity toward {@code direction} in the X and Z axis
     * Gravity is still applied but the entity will not attempt to jump
     * Also update the yaw/pitch of the entity to look along 'direction'
     *
     * @param direction the targeted position
     * @param speed     define how far the entity will move
     */
    public boolean moveTowards(@NotNull Point direction, double speed) {
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
        return physicsResult.collisionX() || physicsResult.collisionY() | physicsResult.collisionZ();
    }

    public void jump(float height) {
        // FIXME magic value
        this.entity.setVelocity(new Vec(0, height * 2.5f, 0));
    }

    /**
     * Retrieves the path to {@code position} and ask the entity to follow the path.
     * <p>
     * Can be set to null to reset the pathfinder.
     * <p>
     * The position is cloned, if you want the entity to continually follow this position object
     * you need to call this when you want the path to update.
     *
     * @param point the position to find the path to, null to reset the pathfinder
     * @param minimumDistance
     * @return true if a path has been found
     */
    public synchronized boolean setPathTo(@Nullable Point point, double minimumDistance) {
        if (point != null && goalPosition != null && point.samePoint(goalPosition) && this.path != null) {
            // Tried to set path to the same value position
            return false;
        }
        final Instance instance = entity.getInstance();
        if (point == null) {
            this.path = null;
            return false;
        }
        // Can't path with a null instance.
        if (instance == null) {
            this.path = null;
            return false;
        }
        // Can't path outside the world border
        final WorldBorder worldBorder = instance.getWorldBorder();
        if (!worldBorder.isInside(point)) {
            return false;
        }
        // Can't path in an unloaded chunk
        final Chunk chunk = instance.getChunkAt(point);
        if (!ChunkUtils.isLoaded(chunk)) {
            return false;
        }

        this.minimumDistance = minimumDistance;
        if (this.entity.getPosition().distance(point) < minimumDistance) return false;
        if (goalPosition != null && point.samePoint(goalPosition)) return false;

        this.path = PFPathGenerator.generate(instance,
                this.entity.getPosition(),
                point,
                100,
                this.entity.getBoundingBox().depth() * 2,
                this.entity.getBoundingBox());

        final boolean success = path != null;
        this.goalPosition = success ? point : null;
        return success;
    }

    private long tick = 0;

    @ApiStatus.Internal
    public synchronized void tick(long tick) {
        if (goalPosition == null) return; // No path
        if (entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) return; // No pathfinding tick for dead entities
        if (path == null) return;

        if (this.tick++ % 5 == 0) {
            sendDebugData();
        }


        Point currentTarget = path.getCurrent();
        if (currentTarget == null) currentTarget = goalPosition;
        float movementSpeed = 0.1f;

        if (this.entity.getDistance(goalPosition) < minimumDistance) return;

        if (entity instanceof LivingEntity living) {
            movementSpeed = living.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue();
        }

        boolean isStuck = moveTowards(currentTarget, movementSpeed);

        if (isStuck) {
            double heightChange = currentTarget.y() - entity.getPosition().y();
            if (heightChange > 0 && entity.getVelocity().y() <= 0) {
                if (heightChange < 0.2) {
                    entity.setVelocity(new Vec(0, 2, 0));
                    moveTowards(currentTarget, movementSpeed);
                } else if (heightChange < 1) {
                    entity.setVelocity(new Vec(0, 4, 0));
                    moveTowards(currentTarget, movementSpeed);
                } else if (jumpCooldown.isReady(tick)) {
                    jumpCooldown.refreshLastUpdate(tick);
                    jump(3.5f);
                    moveTowards(currentTarget, movementSpeed);
                }
            }
        }

        if (entity.getPosition().distanceSquared(currentTarget) < 0.4) {
            path.next();
        }
    }


    /**
     * Gets the value pathfinder position.
     *
     * @return the value pathfinder position, null if there is no one
     */
    public @Nullable Point getGoalPosition() {
        return goalPosition;
    }

    public @NotNull Entity getEntity() {
        return entity;
    }

    public PFPath getPath() {
        return path;
    }

    private void reset() {
        this.goalPosition = null;
        this.path = null;
    }

    public boolean isComplete() {
        if (this.path == null) return true;
        return goalPosition == null || entity.getPosition().distance(goalPosition) < 1;
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

        builder.build()
                .sendTo(entity.getViewersAsAudience());
    }

    private void addPathfinderDebugData(DebugMessage.Builder builder) {
        if (path == null) return;
        var nodes = path.nodes;
        var linePoints = new ArrayList<Vec>();

        for (int i = path.index; i < nodes.size(); i++) {
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
        if (goalPosition == null) return;
        builder.set(
                debugNamespace() + ":pf_target",
                new OutlineBox.Builder()
                        .block(goalPosition.blockX(), goalPosition.blockY(), goalPosition.blockZ(), 0)
                        .color(0x55FF0000)
                        .layer(Layer.TOP)
                        .colorLine(0xFFFF0000)
                        .layerLine(Layer.TOP)
                        .build()
        );
    }
}
