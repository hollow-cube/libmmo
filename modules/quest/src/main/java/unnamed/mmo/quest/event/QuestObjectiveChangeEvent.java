package unnamed.mmo.quest.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.Quest;
import unnamed.mmo.quest.objective.Objective;

public class QuestObjectiveChangeEvent implements PlayerEvent {
    private final Player player;
    private final Quest quest;
    private final Objective objective;

    public QuestObjectiveChangeEvent(Player player, Quest quest, Objective objective) {
        this.player = player;
        this.quest = quest;
        this.objective = objective;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public Quest getQuest() {
        return quest;
    }

    public Objective getObjective() {
        return objective;
    }
}
