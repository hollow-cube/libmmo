package net.hollowcube.entity;

import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.utils.position.PositionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class HeadRotationZombie extends LivingEntity {
    private Point target;

    public HeadRotationZombie() {
        super(EntityType.ZOMBIE);
        onGround = false;
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        if (target == null || getPosition().distance(target) < 0.8) {
            selectTarget();
        }

        moveTowards(target, 0.2f);

    }

    private void selectTarget() {
        target = new Vec(
                ThreadLocalRandom.current().nextInt(-10, 10),
                40,
                ThreadLocalRandom.current().nextInt(-10, 10)
        );
    }

    private void moveTowards(@NotNull Point direction, double speed) {
        final Pos position = getPosition();
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
        final var physicsResult = CollisionUtils.handlePhysics(this, new Vec(speedX, speedY, speedZ));
        refreshPosition(Pos.fromPoint(physicsResult.newPosition()).withView(yaw, pitch));
    }
}

