package unnamed.mmo.quest.objective;

import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;

public record ListObjective(QuestObjective... objectives) implements QuestObjective {

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        // TODO: Implement
        return future;
    }
}
