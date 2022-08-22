package unnamed.mmo.quest.test;

import com.mojang.serialization.Codec;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.objective.QuestObjective;

import java.util.concurrent.CompletableFuture;

public class MockObjective implements QuestObjective {

    private CompletableFuture<Void> future = new CompletableFuture<>();

    private static final Codec<Integer> CURRENT = Codec.INT.orElse(0);

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        context.set(CURRENT, context.get(CURRENT) + 1);
        return future;
    }

    public void complete() {
        future.complete(null);
    }

}
