package unnamed.mmo.quest.test;

import net.minestom.server.entity.Player;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.Quest;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.QuestContextImpl;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.storage.ObjectiveData;

import java.util.Map;

public class MockQuestContext extends QuestContextImpl {

    public MockQuestContext(@NotNull Player player, @NotNull Quest quest, @NotNull ObjectiveData data) {
        super(player, quest, data);
    }

    @Override
    public @NotNull QuestContext child(@NotNull String name, @NotNull QuestObjective objective) {
        if (objective instanceof MockObjective) {
            NamespaceID type = NamespaceID.from("test");
            return children.computeIfAbsent(name, s -> new MockQuestContext(player(), quest(), new ObjectiveData(type, Map.of(), "")));
        } else {
            return super.child(name, objective);
        }
    }
}
