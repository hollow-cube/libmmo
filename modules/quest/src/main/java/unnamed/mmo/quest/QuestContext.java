package unnamed.mmo.quest;

import net.minestom.server.entity.Player;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.objective.QuestRegistry;

public interface QuestContext {
    Player player();
    QuestRegistry.Quest quest();

    QuestContext addChildContext(QuestObjective objective, QuestContext context);

    void set(T thing, blah);

    T get(???);
}
