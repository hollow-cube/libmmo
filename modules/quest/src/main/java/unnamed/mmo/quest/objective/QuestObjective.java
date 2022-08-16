package unnamed.mmo.quest.objective;

import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;

public interface QuestObjective {

    CompletableFuture<Void> onStart(QuestContext context);
}
