package unnamed.mmo.quest;

import com.google.auto.service.AutoService;
import net.kyori.adventure.text.Component;
import net.minestom.server.ServerProcess;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.notifications.Notification;
import net.minestom.server.advancements.notifications.NotificationCenter;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unnamed.mmo.lang.LanguageProvider;
import unnamed.mmo.quest.event.QuestCompleteEvent;
import unnamed.mmo.quest.event.QuestObjectiveChangeEvent;
import unnamed.mmo.quest.objective.Objective;
import unnamed.mmo.quest.player.QuestManager;
import unnamed.mmo.quest.storage.QuestStorage;
import unnamed.mmo.server.Facet;
import unnamed.mmo.server.ServerWrapper;
import unnamed.mmo.util.FutureUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoService(Facet.class)
public class QuestFacet implements Facet {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestFacet.class);

    private final QuestStorage storage = QuestStorage.memory();
    private final Map<UUID, QuestManager> managers = new HashMap<>();

    @Override
    public void hook(@NotNull ServerWrapper server) {
        LOGGER.info("Loaded {} quest objectives", Objective.Factory.REGISTRY.size());

        EventNode<Event> eventNode = EventNode.all("questing");
        server.addEventNode(eventNode);

        eventNode.addListener(PlayerLoginEvent.class, this::handleJoin);
        eventNode.addListener(PlayerDisconnectEvent.class, this::handleLeave);

        eventNode.addListener(QuestObjectiveChangeEvent.class, this::handleObjectiveChange);
        eventNode.addListener(QuestCompleteEvent.class, this::handleQuestCompletion);
    }

    private void handleJoin(@NotNull PlayerLoginEvent event) {
        final UUID player = event.getPlayer().getUuid();
        storage.readQuestData(player)
                .thenAccept(data -> {
                    var manager = new QuestManager(event.getPlayer(), data);
                    managers.put(player, manager);
                    event.getPlayer().sendMessage("Quest data loaded " + data);

                    if (manager.getState("unnamed:test_1") == QuestState.NOT_STARTED) {
                        manager.startQuest("unnamed:test_1");
                    }
                }).exceptionally(FutureUtil::handleException);
    }

    private void handleLeave(@NotNull PlayerDisconnectEvent event) {
        final UUID player = event.getPlayer().getUuid();
        final QuestManager manager = managers.get(player);
        storage.saveQuestData(manager.serialize())
                .exceptionally(FutureUtil::handleException)
                .thenRun(() -> {
                    managers.remove(player);
                    System.out.println("Saved quest data for " + player);
                });
    }

    private void handleObjectiveChange(@NotNull QuestObjectiveChangeEvent event) {
        QuestManager manager = managers.get(event.getPlayer().getUuid());
        Component component = manager.getProgress(event.getQuest().name());
        assert component != null; // Quest must be in progress for this event to trigger.
        event.getPlayer().sendMessage(LanguageProvider.get(component));
    }

    private void handleQuestCompletion(@NotNull QuestCompleteEvent event) {
        var notification = new Notification(
                Component.text("Completed " + event.getQuest().name()),
                FrameType.CHALLENGE,
                Material.GOLD_INGOT
        );
        NotificationCenter.send(notification, event.getPlayer());
    }

}
