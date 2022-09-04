package unnamed.mmo.entity.brain.task;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

import java.util.concurrent.ThreadLocalRandom;

public class WanderInRegionTask extends AbstractTask {
    private final Spec spec;

    public WanderInRegionTask(@NotNull Spec spec) {
        this.spec = spec;
    }

    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);

        var target = new Vec(
                ThreadLocalRandom.current().nextInt(-10, 10),
                40,
                ThreadLocalRandom.current().nextInt(-10, 10)
        );
        System.out.println("PF to " + target);

        boolean result = brain.setPathTo(target);
        System.out.println("Result: " + result);

        if (!result) end(false);
    }

    @Override
    public void tick(@NotNull Brain brain, long time) {
        if (brain.navigator().isActive()) return;
        end(true);
    }


    public record Spec() implements Task.Spec {
        public static final Codec<Spec> CODEC = Codec.unit(new Spec());

        @Override
        public @NotNull Task create() {
            return new WanderInRegionTask(this);
        }
    }

    @AutoService(Task.Factory.class)
    public static class Factory extends Task.Factory {
        public Factory() {
            super("unnamed:wander_in_region", Spec.class, Spec.CODEC);
        }
    }
}
