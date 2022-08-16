package unnamed.mmo.quest.objective;

import net.minestom.server.event.EventListener;
import unnamed.mmo.quest.ProgressAttainedEvent;
import unnamed.mmo.quest.QuestContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public record ProgressObjective(String flag) implements QuestObjective {


    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

        context.player().eventNode().addListener(EventListener.builder(ProgressAttainedEvent.class)
                .expireWhen(event -> complete.isDone())
                .filter(event -> event.getPlayer() == context.player() &&
                        Objects.equals(event.getProgressId(), flag))
                .handler(event -> complete.complete(null))
                .build());

        return complete;
    }
}
