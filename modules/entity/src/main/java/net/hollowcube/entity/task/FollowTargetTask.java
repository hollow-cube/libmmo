package net.hollowcube.entity.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.hollowcube.entity.SmartEntity;
import net.minestom.server.entity.Entity;
import net.minestom.server.thread.TickThread;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class FollowTargetTask extends AbstractTask {

    private Cooldown attackCooldown = new Cooldown(Duration.of(1, TimeUnit.SECOND));

    @Override
    public void tick(@NotNull SmartEntity entity, long time) {

        final Entity target = entity.getTarget();
        if (target == null) {
            end(true);
            return;
        }

        TickThread thread = TickThread.current();
        Check.notNull(thread, "Task ticked outside of tick thread");
        if (thread.getTick() % 5 == 0) {
            entity.navigator().setPathTo(target.getPosition());
        }

        double distance = entity.getDistanceSquared(target);
        if (distance < 4 && attackCooldown.isReady(time)) {
            attackCooldown.refreshLastUpdate(time);
            entity.attack(target);
        }
    }


    public record Spec() implements Task.Spec {
        public static final Codec<Spec> CODEC = Codec.unit(new Spec());

        @Override
        public @NotNull Task create() {
            return new FollowTargetTask();
        }

        @Override
        public @NotNull NamespaceID namespace() {
            return NamespaceID.from("unnamed:follow_target");
        }
    }

    @AutoService(Task.Factory.class)
    public static class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:follow_target", Spec.class, Spec.CODEC);
        }
    }
}
