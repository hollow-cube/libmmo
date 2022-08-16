package unnamed.mmo.quest.objective;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;
import unnamed.mmo.quest.QuestProgress;

public interface QuestObjective {

    default void onMobKill(LivingEntity entity) {}

    default void onBlockBreak(Block block) {}

    default void onProgressComplete(QuestProgress progress) {}

    boolean isObjectiveComplete();
}
