package unnamed.mmo.quest.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.Quest;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.QuestContextImpl;
import unnamed.mmo.quest.QuestState;
import unnamed.mmo.quest.event.QuestCompleteEvent;
import unnamed.mmo.quest.storage.ObjectiveData;
import unnamed.mmo.quest.storage.QuestData;

import java.util.*;

public class QuestManager {
    private final Player player;

    private final Set<String> completed = new HashSet<>();
    public final Map<String, QuestContext> inProgress = new HashMap<>(); //todo should be private

    public QuestManager(@NotNull Player player, @NotNull QuestData data) {
        this.player = player;
        this.loadFromData(data);
    }

    public @NotNull QuestState getState(@NotNull String quest) {
        if (completed.contains(quest))
            return QuestState.COMPLETED;
        if (inProgress.containsKey(quest))
            return QuestState.IN_PROGRESS;
        return QuestState.NOT_STARTED;
    }

    public void startQuest(@NotNull String questId) {
        Quest quest = Quest.fromNamespaceId(questId);
        Check.notNull(quest, "No such quest: " + questId);
        QuestContextImpl context = new QuestContextImpl(player, quest, new ObjectiveData(Map.of(), ""));
        startWithTracking(quest, context);
    }

    public @NotNull QuestData serialize() {
        Map<String, ObjectiveData> inProgress = new HashMap<>();
        for (var entry : this.inProgress.entrySet()) {
            inProgress.put(entry.getKey(), entry.getValue().serialize());
        }
        return new QuestData(player.getUuid(), List.copyOf(completed), inProgress);
    }

    private void loadFromData(QuestData data) {
        completed.addAll(data.completed());
        for (var entry : data.inProgress().entrySet()) {
            final var questId = entry.getKey();
            final var objectiveData = entry.getValue();

            Quest quest = Quest.fromNamespaceId(questId);
            //todo better handle errors like these (probably just leave the inProgress state and do not parse)
            Check.notNull(quest, "no such quest: " + questId);

            // Create a context from the data and restart the quest
            QuestContext context = new QuestContextImpl(player, quest, objectiveData);
            startWithTracking(quest, context);
        }
    }

    private void startWithTracking(@NotNull Quest quest, @NotNull QuestContext rootContext) {
        final String questId = quest.name();
        inProgress.put(questId, rootContext);

        var completion = quest.objective().onStart(rootContext);
        completion.thenAccept(unused -> {
            completed.add(questId);
            inProgress.remove(questId);

            // Dispatch completion event
            var event = new QuestCompleteEvent(player, quest);
            EventDispatcher.call(event);
        });
    }

}
