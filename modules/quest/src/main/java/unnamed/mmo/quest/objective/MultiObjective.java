package unnamed.mmo.quest.objective;

import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.QuestContextImpl;

import java.util.concurrent.CompletableFuture;

public record MultiObjective(QuestObjective... objectives) implements QuestObjective {
    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void>[] futures = new CompletableFuture[objectives.length];
        for (int i = 0; i < objectives().length; i++) {
//            QuestContext childContext = new QuestContextImpl(context);
//            context.addChildContext(objectives[i], childContext);
            futures[i] = objectives[i].onStart(context);
        }
        return CompletableFuture.allOf(futures);
    }
}
