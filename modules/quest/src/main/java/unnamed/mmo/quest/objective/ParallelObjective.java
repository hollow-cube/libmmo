package unnamed.mmo.quest.objective;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kyori.adventure.text.Component;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.event.QuestObjectiveCompleteEvent;
import unnamed.mmo.util.EventUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static unnamed.mmo.dfu.ExtraCodecs.lazy;

public record ParallelObjective(List<Objective> children) implements Objective {
    public static final Codec<ParallelObjective> CODEC = RecordCodecBuilder.create(i -> i.group(
            lazy(() -> Objective.CODEC).listOf().fieldOf("children").forGetter(ParallelObjective::children)
    ).apply(i, ParallelObjective::new));

    public ParallelObjective {
        Check.argCondition(children.isEmpty(), "children must not be empty");
        // Max children of 32 because we use an integer bitset for tracking completion
        Check.argCondition(children.size() > 32, "cannot have more than 32 children");
    }

    private static final Codec<Integer> COMPLETED = Codec.INT.orElse(0);

    @Override
    public @NotNull CompletableFuture<Void> onStart(@NotNull QuestContext context) {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[children.size()];

        // Completed is a bitset of the completed children. A one at the bit index
        // of a child indicates that the child has already been completed.
        int completed = context.get(COMPLETED);
        for (int i = 0; i < children.size(); i++) {
            // If already complete, ignore it
            if ((completed & (1 << i)) != 0) {
                futures[i] = CompletableFuture.completedFuture(null);
                continue;
            }

            // Not complete, restart this child.
            Objective child = children().get(i);
            QuestContext childContext = context.child(String.valueOf(i));

            final int index = i;
            futures[i] = child.onStart(childContext).thenRun(() -> {
                // Update the completion bitset, using the latest version
                synchronized (context) {
                    context.set(COMPLETED, context.get(COMPLETED) | (1 << index));
                }

                //todo could delete the context data for this child at this point.

                // Dispatch completion event
                var event = new QuestObjectiveCompleteEvent(context.player(), context.quest(), child);
                EventUtil.safeDispatch(event);
            });
        }

        return CompletableFuture.allOf(futures);
    }

    @Override
    public @NotNull Component getCurrentStatus(@NotNull QuestContext context) {
        return Component.text("not implemented");
    }

    @AutoService(Objective.Factory.class)
    public static class Factory extends Objective.Factory {
        public Factory() {
            super(NamespaceID.from("unnamed:parallel"), ParallelObjective.class, ParallelObjective.CODEC);
        }
    }
}
