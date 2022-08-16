package unnamed.mmo.quest.objective;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.QuestProgress;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public record MultiObjective(QuestObjective... objectives) implements QuestObjective {
    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void>[] futures = new CompletableFuture[objectives.length];
        for (int i = 0; i < objectives().length; i++) {
            futures[i] = objectives[i].onStart(context);
        }
        return CompletableFuture.allOf(futures);
    }
}
