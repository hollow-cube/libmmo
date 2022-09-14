package net.hollowcube.quest.test;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.quest.Quest;
import net.hollowcube.quest.QuestContextImpl;
import net.hollowcube.quest.objective.ObjectiveData;

public class MockQuestContext extends QuestContextImpl {

    public MockQuestContext(@NotNull Player player, @NotNull Quest quest, @NotNull ObjectiveData data) {
        super(player, quest, data);
    }

}
