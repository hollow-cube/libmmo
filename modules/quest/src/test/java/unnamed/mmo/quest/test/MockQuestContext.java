package unnamed.mmo.quest.test;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.Quest;
import unnamed.mmo.quest.QuestContextImpl;
import unnamed.mmo.quest.objective.ObjectiveData;

public class MockQuestContext extends QuestContextImpl {

    public MockQuestContext(@NotNull Player player, @NotNull Quest quest, @NotNull ObjectiveData data) {
        super(player, quest, data);
    }

}
