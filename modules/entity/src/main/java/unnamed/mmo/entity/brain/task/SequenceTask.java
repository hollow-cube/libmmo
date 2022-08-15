package unnamed.mmo.entity.brain.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static unnamed.mmo.util.ExtraCodecs.lazy;

public class SequenceTask extends AbstractTask {
    public static final Codec<SequenceTask> CODEC = RecordCodecBuilder.create(i -> i.group(
            lazy(() -> Task.CODEC).listOf().fieldOf("children").forGetter(SequenceTask::children)
    ).apply(i, SequenceTask::new));

    private final List<Task> children;

    // Running state
    private Iterator<Task> taskIter = Collections.emptyIterator();
    private Task current = null;

    public SequenceTask(@NotNull List<Task> children) {
        this.children = List.copyOf(children);
    }

    public @NotNull List<Task> children() {
        return children;
    }

    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);

        taskIter = children().iterator();
        if (!taskIter.hasNext()) {
            end(false);
            return;
        }

        current = taskIter.next();
        current.start(brain);
    }

    @Override
    public void tick(@NotNull Brain brain) {
        current.tick(brain);

        // Do nothing if current task is still running
        if (current.getState() == State.RUNNING) return;

        // If current task failed, fail the sequence
        if (current.getState() == State.FAILED) {
            end(false);
            return;
        }

        // If there are no more tasks, exit
        if (!taskIter.hasNext()) {
            end(true);
            return;
        }

        // Next task
        current = taskIter.next();
        current.start(brain);
    }

    @Override
    public @NotNull Task deepCopy() {
        return new SequenceTask(children().stream().map(Task::deepCopy).toList());
    }


    @AutoService(Task.Factory.class)
    public static final class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:sequence", SequenceTask.class, SequenceTask.CODEC);
        }
    }
}
