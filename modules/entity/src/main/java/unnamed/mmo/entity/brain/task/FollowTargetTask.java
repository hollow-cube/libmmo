package unnamed.mmo.entity.brain.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

public class FollowTargetTask extends AbstractTask {

    private int tick = 0;

    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);
    }

    @Override
    public void tick(@NotNull Brain brain) {
        final Entity target = brain.getTarget();
        if (target == null) {
            end(true);
            return;
        }

        if (tick++ % 5 == 0) {
            brain.setPathTo(target.getPosition());
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
