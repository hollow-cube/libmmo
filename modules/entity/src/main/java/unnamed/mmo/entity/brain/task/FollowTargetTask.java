package unnamed.mmo.entity.brain.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.UnnamedEntity;
import unnamed.mmo.entity.brain.Brain;

import java.time.Duration;

public class FollowTargetTask extends AbstractTask {

    private int tick = 0;

    private Cooldown attackCooldown = new Cooldown(Duration.of(1, TimeUnit.SECOND));

    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);
    }

    @Override
    public void tick(@NotNull Brain brain, long time) {

        final Entity target = brain.getTarget();
        if (target == null) {
            end(true);
            return;
        }

        if (tick++ % 5 == 0) {
            brain.setPathTo(target.getPosition());
        }

        double distance = brain.entity().getDistanceSquared(target);
        if (distance < 4 && attackCooldown.isReady(time)) {
            attackCooldown.refreshLastUpdate(time);
            ((UnnamedEntity) brain.entity()).attack(target);
        }
    }


    public record Spec() implements Task.Spec {
        public static final Codec<Spec> CODEC = Codec.unit(new Spec());

        @Override
        public @NotNull Task create() {
            return new FollowTargetTask();
        }
    }

    @AutoService(Task.Factory.class)
    public static class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:follow_target", Spec.class, Spec.CODEC);
        }
    }
}
