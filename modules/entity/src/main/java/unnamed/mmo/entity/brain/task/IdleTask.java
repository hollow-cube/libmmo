package unnamed.mmo.entity.brain.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.entity.brain.Brain;

public class IdleTask extends AbstractTask {
    public static final Codec<IdleTask> CODEC = RecordCodecBuilder.create(i -> i.group(
            NumberProvider.CODEC.fieldOf("time").forGetter(IdleTask::time)
    ).apply(i, IdleTask::new));

    private final NumberProvider time;

    // Running state
    private int sleepTime = 0;

    public IdleTask(@NotNull NumberProvider time) {
        this.time = time;
    }

    public NumberProvider time() {
        return time;
    }

    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);

        //todo get number source from entity or something, this is inconvenient to test
        sleepTime = (int) time.nextLong(NumberSource.threadLocalRandom());
    }

    @Override
    public void tick(@NotNull Brain brain) {
        sleepTime -= 1;
        if (sleepTime < 1) {
            end(true);
        }
    }

    @Override
    public @NotNull Task deepCopy() {
        return new IdleTask(time);
    }


    @AutoService(Task.Factory.class)
    public static final class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:idle", IdleTask.class, IdleTask.CODEC);
        }
    }

}
