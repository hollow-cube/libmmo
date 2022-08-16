package unnamed.mmo.quest.objective;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;

public abstract class QuestObjective {

    void onMobKill(LivingEntity entity) {}

    void onBlockBreak(Block block) {}

    boolean isObjectiveComplete() { return false; }
}
