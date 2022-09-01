package unnamed.mmo.entity.brain.task;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.mql.MqlScript;
import unnamed.mmo.util.DFUUtil;

import java.util.List;
import java.util.function.Function;

import static unnamed.mmo.util.ExtraCodecs.lazy;

public class SelectorTask extends AbstractTask {

    private final Spec spec;
//    private final Task targetTask;
//    private final Task otherwiseTask;

    private Task activeTask;

    public SelectorTask(Spec spec) {
        this.spec = spec;
//        this.targetTask = spec.target().create();
//        this.otherwiseTask = spec.otherwise().create();
    }

    @Override
    public void tick(@NotNull Brain brain) {
//        final Entity target = brain.getTarget();
//        // Check if there is a value, and we are not currently running the value task.
//        if (target != null && activeTask != targetTask) {
//            selectAndStart(brain, targetTask);
//            return;
//        }
//        if (target == null && activeTask != otherwiseTask) {
//            selectAndStart(brain, otherwiseTask);
//            return;
//        }
//
//        // Restart a task if it has finished
//        if (activeTask.getState() != State.RUNNING) {
//            activeTask.start(brain);
//        }
//
//        activeTask.tick(brain);
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

    public record Descriptor(
            MqlScript script,
            Task.Spec task,
            boolean canInterrupt
    ) implements Task.Spec {
        @Override
        public @NotNull Task create() {
            return task.create();
        }
    }

    public record Spec(
            //todo stimuli
            List<Descriptor> children
    ) implements Task.Spec {
        public static final Codec<Pair<Task.Spec, Boolean>> abcx = Codec.pair(
                lazy(() -> Task.Spec.CODEC),
                RecordCodecBuilder.create(i -> i.group(
                        Codec.BOOL.fieldOf("canInterrupt").forGetter(b -> b)
                ).apply(i, b -> b))
        );
//        public static final Codec<Spec> CODEC = RecordCodecBuilder.create(i -> i.group(
//                Codec.unboundedMap(MqlScript.CODEC, Codec.pair(lazy(() -> Task.Spec.CODEC), Codec.BOOL.fieldOf("").forGetter(it -> false)))
//                        .xmap(DFUUtil::mapToPairList, DFUUtil::pairListToMap)
//                        .fieldOf("children").forGetter(Spec::children)
//        ).apply(i, Spec::new));

        @Override
        public @NotNull Task create() {
            return new SelectorTask(this);
        }
    }

    public static void main(String[] args) {
//        var result = JsonOps.INSTANCE.withDecoder(Spec.CODEC)
//                .apply(JsonParser.parseString("""
//                        {
//                            "children": {
//                                "1.0": "abc",
//                                "q.is_alive": "def",
//                                "3.0": "ghi"
//                            }
//                        }"""))
//                .result()
//                .get()
//                .getFirst();
        var result2 = JsonOps.INSTANCE.withDecoder(Spec.abcx)
                .apply(JsonParser.parseString("""
                        {
                            "type": "unnamed:idle",
                            "time": 1,
                            "canInterrupt": false
                        }"""))
                .result()
                .get()
                .getFirst();

        System.out.println(result2);

//        for (var entry : result.children) {
//            System.out.println(entry.getSecond());
//        }
    }


}
