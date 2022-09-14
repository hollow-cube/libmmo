package net.hollowcube.quest.event;

import net.hollowcube.quest.Quest;
import net.hollowcube.quest.objective.Objective;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class QuestObjectiveCompleteEvent implements PlayerEvent {
    private final Player player;
    private final Quest quest;
    private final Objective objective;

    public QuestObjectiveCompleteEvent(Player player, Quest quest, Objective objective) {
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
