package net.hollowcube.quest.event;

import net.hollowcube.quest.Quest;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent implements PlayerEvent {
    private final Player player;
    private final Quest quest;


    public QuestCompleteEvent(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Quest getQuest() {
        return quest;
    }
}
