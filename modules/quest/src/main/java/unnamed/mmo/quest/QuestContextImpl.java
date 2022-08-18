package unnamed.mmo.quest;

import net.minestom.server.entity.Player;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.objective.QuestRegistry;

import java.util.HashMap;
import java.util.Map;

public class QuestContextImpl implements QuestContext {

    private final Player player;
    private final QuestRegistry.Quest quest;
    // A root object to a tree of QuestContext
    private final Map<QuestObjective, QuestContext> contextTree;

    public QuestContextImpl(Player player, QuestRegistry.Quest quest) {
        this.player = player;
        this.quest = quest;
        this.contextTree = new HashMap<>();
    }

    public QuestContextImpl(QuestContext context) {
        this.player = context.player();
        this.quest = context.quest();
        this.contextTree = new HashMap<>();
    }

    @Override
    public QuestContext addChildContext(QuestObjective objective, QuestContext context) {
        contextTree.put(objective, context);
        return this;
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public QuestRegistry.Quest quest() {
        return quest;
    }
}
