package unnamed.mmo.entity.brain.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

import static unnamed.mmo.util.ExtraCodecs.lazy;

public class SelectorTask extends AbstractTask {

    private final Spec spec;
    private final Task targetTask;
    private final Task otherwiseTask;

    private Task activeTask;

    public SelectorTask(Spec spec) {
        this.spec = spec;
        this.targetTask = spec.target().create();
        this.otherwiseTask = spec.otherwise().create();
    }

    @Override
    public void tick(@NotNull Brain brain) {
        final Entity target = brain.getTarget();
        // Check if there is a value, and we are not currently running the value task.
        if (target != null && activeTask != targetTask) {
            selectAndStart(brain, targetTask);
            return;
        }
        if (target == null && activeTask != otherwiseTask) {
            selectAndStart(brain, otherwiseTask);
            return;
        }

        // Restart a task if it has finished
        if (activeTask.getState() != State.RUNNING) {
            activeTask.start(brain);
        }

        activeTask.tick(brain);
    }

    private void selectAndStart(Brain brain, Task task) {
        // End the active task
        if (activeTask != null) {
            //todo bad
            ((AbstractTask) activeTask).end(true);
        }

        // start the other task
        activeTask = task;
        activeTask.start(brain);
    }


    public record Spec(
            Task.Spec target,
            Task.Spec otherwise
    ) implements Task.Spec {
        public static final Codec<Spec> CODEC = RecordCodecBuilder.create(i -> i.group(
                lazy(() -> Task.Spec.CODEC).fieldOf("target").forGetter(Spec::target),
                lazy(() -> Task.Spec.CODEC).fieldOf("otherwise").forGetter(Spec::target)
        ).apply(i, Spec::new));

        @Override
        public @NotNull Task create() {
            return new SelectorTask(this);
        }
    }


}
