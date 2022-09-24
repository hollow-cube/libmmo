package net.hollowcube.entity.brain.task;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.entity.brain.stimuli.NearbyEntityStimuliSource;
import net.hollowcube.entity.brain.stimuli.StimuliSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hollowcube.entity.EntityMqlQueryContext;
import net.hollowcube.entity.UnnamedEntity;
import net.hollowcube.entity.brain.Brain;
import net.hollowcube.mql.MqlScript;

import java.util.ArrayList;
import java.util.List;

import static net.hollowcube.dfu.ExtraCodecs.lazy;

public class SelectorTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectorTask.class);

    private final Spec spec;
    private final List<Task> children = new ArrayList<>();

    private EntityMqlQueryContext queryContext;
    private int activeTask = -1;

    //todo temp
    private final StimuliSource stimuli = new NearbyEntityStimuliSource();

    public SelectorTask(Spec spec) {
        this.spec = spec;
        for (var child : spec.children) {
            children.add(child.create());
        }
    }


    @Override
    public void start(@NotNull Brain brain) {
        super.start(brain);
        this.queryContext = new EntityMqlQueryContext((UnnamedEntity) brain.entity());

        evaluate(brain);
    }

    @Override
    public void tick(@NotNull Brain brain, long time) {
        stimuli.update(brain); //todo not sure these should update every tick?

        // Try to tick the current task, if present
        if (activeTask != -1) {
            Task active = children.get(activeTask);
            active.tick(brain, time);

            // Check if task is complete
            switch (active.getState()) {
                case FAILED -> end(false);
                case COMPLETE -> {
                    // Current task finished with success, select a new task.
                    activeTask = -1; // Reset the active task so evaluate restarts it if relevant
                    evaluate(brain);
                    return;
                }
                // Otherwise do nothing and continue as normal
            }

            // If the current task cannot be interrupted, do nothing else
            Descriptor desc = spec.children.get(activeTask);
            if (!desc.canInterrupt()) return;
        }

        // Attempt to choose a new task.
        //todo do not test for change every tick in the future
        evaluate(brain);
    }

    /** Evaluate each task in order, choosing the first matching one. */
    private void evaluate(@NotNull Brain brain) {
        for (int i = 0; i < children.size(); i++) {
            Descriptor child = spec.children.get(i);
            // Try to evaluate this child
            if (!child.script.evaluateToBool(queryContext))
                continue;

            // If the selected task is also the current task, do nothing
            if (activeTask == i) return;

            // Cancel the old task
            //todo should do this better
            brain.navigator().setPathTo(null);

            // Change task and start the new one
            LOGGER.info("starting new task: {}", i);
            activeTask = i;
            Task newTask = children.get(i);
            newTask.start(brain);
            return;
        }
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
        private static final Codec<Pair<Task.Spec, Boolean>> TASK_MIXIN = Codec.pair(
                lazy(() -> Task.Spec.CODEC),
                RecordCodecBuilder.create(i -> i.group(
                        Codec.BOOL.optionalFieldOf("canInterrupt", false).forGetter(b -> b)
                ).apply(i, b -> b))
        );

        private static final Codec<List<Descriptor>> DESCRIPTOR_LIST = Codec.unboundedMap(MqlScript.CODEC, TASK_MIXIN)
                .xmap(m -> m.entrySet().stream().map(entry -> new Descriptor(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond())).toList(),
                        d -> {throw new RuntimeException("not implemented");});

        public static final Codec<Spec> CODEC = RecordCodecBuilder.create(i -> i.group(
                DESCRIPTOR_LIST.fieldOf("children").forGetter(Spec::children)
        ).apply(i, Spec::new));


        @Override
        public @NotNull Task create() {
            return new SelectorTask(this);
        }
    }



}
