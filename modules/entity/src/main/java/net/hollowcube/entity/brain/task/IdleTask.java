package net.hollowcube.entity.brain.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.data.NumberSource;
import net.hollowcube.data.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.entity.brain.Brain;

public class IdleTask extends AbstractTask {
    private final Spec spec;
    private int sleepTime = 0;

    public IdleTask(@NotNull Spec spec) {
        this.spec = spec;
    }

    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);

        //todo get number source from entity or something, this is inconvenient to test
        sleepTime = (int) spec.time().nextLong(NumberSource.threadLocalRandom());
    }

    @Override
    public void tick(@NotNull Brain brain, long time) {
        sleepTime -= 1;
        if (sleepTime < 1) {
            end(true);
        }
    }


    public record Spec(
            @NotNull NumberProvider time
    ) implements Task.Spec {

        public static final Codec<Spec> CODEC = RecordCodecBuilder.create(i -> i.group(
                NumberProvider.CODEC.fieldOf("time").forGetter(Spec::time)
        ).apply(i, Spec::new));

        @Override
        public @NotNull Task create() {
            return new IdleTask(this);
        }
    }

    @AutoService(Task.Factory.class)
    public static final class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:idle", Spec.class, Spec.CODEC);
        }
    }

}
