package unnamed.mmo.quest.objective;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kyori.adventure.text.Component;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.event.QuestObjectiveCompleteEvent;
import unnamed.mmo.util.EventUtil;
import unnamed.mmo.util.FutureUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static unnamed.mmo.util.ExtraCodecs.lazy;

public record SequenceObjective(List<QuestObjective> children) implements QuestObjective {

    public static final Codec<SequenceObjective> CODEC = RecordCodecBuilder.create(i -> i.group(
            lazy(() -> QuestObjective.CODEC).listOf().fieldOf("children").forGetter(SequenceObjective::children)
    ).apply(i, SequenceObjective::new));

    private static final Codec<Integer> CURRENT = Codec.INT.orElse(0);

    public SequenceObjective {
        //todo not sure this should be an error?
        Check.argCondition(children.isEmpty(), "children must not be empty");
    }

    @Override
    public @NotNull CompletableFuture<Void> onStart(@NotNull QuestContext context) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        int current = context.get(CURRENT);
        for (int i = current; i < children().size(); i++) {
            QuestObjective child = children().get(i);
            QuestContext childContext = context.child(String.valueOf(i));

            final int index = i;
            future = future.thenCompose(unused -> child.onStart(childContext)).thenRun(() -> {
                context.set(CURRENT, index + 1);
                // Dispatch completion event
                var event = new QuestObjectiveCompleteEvent(context.player(), context.quest(), child);
                EventUtil.safeDispatch(event);
            });
        }

        return future.exceptionally(FutureUtil::handleException);
    }

    @Override
    public @Nullable Component getCurrentStatus(@NotNull QuestContext context) {
        int current = context.get(CURRENT);
        // If the quest is completed we can just keep showing the last child
        if (current >= children.size()) current = children.size() - 1;

        QuestObjective currentChild = children.get(current);
        QuestContext currentChildContext = context.child(String.valueOf(current));
        return currentChild.getCurrentStatus(currentChildContext);
    }


    @AutoService(QuestObjective.Factory.class)
    public static class Factory extends QuestObjective.Factory {
        public Factory() {
            super(NamespaceID.from("unnamed:sequence"), SequenceObjective.class, SequenceObjective.CODEC);
        }
    }
}
