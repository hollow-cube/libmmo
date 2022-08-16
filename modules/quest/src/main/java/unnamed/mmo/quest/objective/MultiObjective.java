package unnamed.mmo.quest.objective;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;
import unnamed.mmo.quest.QuestProgress;

import java.util.Collections;
import java.util.List;

public class MultiObjective implements QuestObjective {

    private final List<QuestObjective> subObjectives;

    public MultiObjective(QuestObjective... objectives) {
        subObjectives = List.of(objectives);
    }

    public MultiObjective(List<QuestObjective> objectives) {
        subObjectives = Collections.unmodifiableList(objectives);
    }

    @Override
    public void onMobKill(LivingEntity entity) {
        for(QuestObjective objective : subObjectives) {
            objective.onMobKill(entity);
        }
    }

    @Override
    public void onBlockBreak(Block block) {
        for(QuestObjective objective : subObjectives) {
            objective.onBlockBreak(block);
        }
    }

    @Override
    public void onProgressComplete(QuestProgress progress) {
        for(QuestObjective objective : subObjectives) {
            objective.onProgressComplete(progress);
        }
    }

    @Override
    public boolean isObjectiveComplete() {
        for(QuestObjective objective : subObjectives) {
            if(!objective.isObjectiveComplete()) return false;
        }
        return true;
    }
}
