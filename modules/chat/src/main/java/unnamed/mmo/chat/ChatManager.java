package unnamed.mmo.chat;

import com.google.auto.service.AutoService;
import net.minestom.server.ServerProcess;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import unnamed.mmo.chat.command.LogCommand;
import unnamed.mmo.chat.storage.ChatStorage;
import unnamed.mmo.server.Facet;
import unnamed.mmo.util.EventUtil;

import java.time.Instant;

@AutoService(Facet.class)
public class ChatManager implements Facet {
    //todo generally not a fan of this class. Need to come up with a bit of a better standard for these "manager" classes
    //     perhaps using SPI would be nice. Need to be careful about testability though
    //     See Facet, i made some notes there

    //todo where does this come from?
    private static final String SERVER_NAME = "test_server";
    private static final String DEFAULT_CHANNEL = "global";
    private static final String COMMAND_CHANNEL = "command";

    private final EventNode<Event> eventNode = EventUtil
            .notCancelledNode("chat")
            // Very low priority to run other events which might cancel these beforehand
            .setPriority(-10);

    private final ChatStorage storage;

    public ChatManager() {
        //todo this should be based on a config param, which should be loaded before loading managers
        this(ChatStorage.noop());
    }

    @TestOnly
    public ChatManager(@NotNull ChatStorage storage) {
        this.storage = storage;

        eventNode.addListener(PlayerChatEvent.class, this::handleChatEvent);
        eventNode.addListener(PlayerCommandEvent.class, this::handleCommandEvent);
    }

    @Override
    public void hook(@NotNull ServerProcess server) {
        server.eventHandler().addChild(eventNode);

        server.command().register(new LogCommand(storage));
    }

    public EventNode<Event> eventNode() {
        return eventNode;
    }

    private void handleChatEvent(PlayerChatEvent event) {
        // Record message, ignore response.
        storage.recordChatMessage(new ChatMessage(
                Instant.now(),
                SERVER_NAME,
                DEFAULT_CHANNEL,
                event.getPlayer().getUuid(),
                event.getMessage()
        )).exceptionally(ex -> {
            //todo what to do on exception? I guess send to sentry or some other such service
            ex.printStackTrace();
            return null;
        });
    }

    private void handleCommandEvent(PlayerCommandEvent event) {
        // Record command, ignore response.
        storage.recordChatMessage(new ChatMessage(
                Instant.now(),
                SERVER_NAME,
                COMMAND_CHANNEL,
                event.getPlayer().getUuid(),
                event.getCommand()
        )).exceptionally(ex -> {
            //todo what to do on exception?
            ex.printStackTrace();
            return null;
        });
    }
}
