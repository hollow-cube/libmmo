package net.hollowcube.entity.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.entity.SmartEntity;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.hollowcube.dfu.ExtraCodecs.lazy;

public class SequenceTask extends AbstractTask {

    private final Spec spec;
    private final List<Task> children;
    private int current = 0;

    public SequenceTask(@NotNull Spec spec) {
        this.spec = spec;
        this.children = spec.children()
                .stream()
                .map(Task.Spec::create)
                .toList();
    }

    @Override
    public void start(@NotNull SmartEntity entity) {
        super.start(entity);

        reset();
        if (!hasNext()) {
            end(false);
            return;
        }

        current().start(entity);
    }

    @Override
    public void tick(@NotNull SmartEntity entity, long time) {
        final Task current = current();
        current.tick(entity, time);

        // Do nothing if current task is still running
        if (current.getState() == State.RUNNING) return;

        // If current task failed, fail the sequence
        if (current.getState() == State.FAILED) {
            end(false);
            return;
        }

        // If there are no more tasks, exit
        if (!hasNext()) {
            end(true);
            return;
        }

        // Next task
        next().start(entity);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasNext() {
        return current < children.size() - 1;
    }

    private Task current() {
        return children.get(current);
    }

    private Task next() {
        return children.get(++current);
    }

    private void reset() {
        this.current = 0;
    }


    public record Spec(
            List<Task.Spec> children
    ) implements Task.Spec {
        public static final Codec<Spec> CODEC = RecordCodecBuilder.create(i -> i.group(
                lazy(() -> Task.Spec.CODEC).listOf().fieldOf("children").forGetter(Spec::children)
        ).apply(i, Spec::new));

        @Override
        public @NotNull Task create() {
            return new SequenceTask(this);
        }

        @Override
        public @NotNull NamespaceID namespace() {
            return NamespaceID.from("unnamed:sequence");
        }
    }

    @AutoService(Task.Factory.class)
    public static final class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:sequence", Spec.class, Spec.CODEC);
        }
    }
}
