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
        // Check if there is a target, and we are not currently running the target task.
        if (target != null && activeTask != targetTask) {
            // End the active task
            if (activeTask != null) {
                //todo bad
                ((AbstractTask) activeTask).end(true);
            }

            // start the other task
            activeTask = otherwiseTask;
            activeTask.start(brain);
        } else if (activeTask != otherwiseTask)

        activeTask.tick(brain);
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
