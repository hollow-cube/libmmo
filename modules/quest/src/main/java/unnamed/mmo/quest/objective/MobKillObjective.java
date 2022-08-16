package unnamed.mmo.quest.objective;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;

public class MobKillObjective implements QuestObjective {

    private final EntityType killType;
    private final int count;
    private int current;

    public MobKillObjective(EntityType type, int count) {
        killType = type;
        this.count = count;
        current = 0;
    }

    @Override
    public void onMobKill(LivingEntity entity) {
        if(entity.getEntityType() == killType) {
            current++;
        }
    }

    @Override
    public boolean isObjectiveComplete() {
        return current >= count;
    }
}
