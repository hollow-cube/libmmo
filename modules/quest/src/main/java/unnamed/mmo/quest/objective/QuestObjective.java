package unnamed.mmo.quest.objective;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.QuestProgress;

import java.util.concurrent.CompletableFuture;

public interface QuestObjective {

    CompletableFuture<Void> onStart(QuestContext context);
}
