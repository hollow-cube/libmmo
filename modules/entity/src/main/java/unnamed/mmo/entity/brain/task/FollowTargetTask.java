package unnamed.mmo.entity.brain.task;

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

        @Override
        public @NotNull Task create() {
            return new FollowTargetTask();
        }
    }
}
