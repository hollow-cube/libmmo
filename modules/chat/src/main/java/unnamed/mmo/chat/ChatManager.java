package unnamed.mmo.chat;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.chat.storage.ChatStorage;
import unnamed.mmo.util.EventUtil;

import java.time.Instant;

public class ChatManager {
    private static final String DEFAULT_CHANNEL = "global";
    private static final String COMMAND_CHANNEL = "command";

    private final EventNode<Event> eventNode = EventUtil
            .notCancelledNode("chat")
            // Very low priority to run other events which might cancel these beforehand
            .setPriority(-10);
    private final ChatStorage storage;

    public ChatManager(@NotNull ChatStorage storage) {
        this.storage = storage;

        eventNode.addListener(PlayerChatEvent.class, this::handleChatEvent);
        eventNode.addListener(PlayerCommandEvent.class, this::handleCommandEvent);
    }

    public @NotNull EventNode<Event> eventNode() {
        return eventNode;
    }


    private void handleChatEvent(PlayerChatEvent event) {
        // Record message, ignore response.
        storage.recordChatMessage(new ChatMessage(
                Instant.now(),
                "test-server",
                DEFAULT_CHANNEL,
                event.getPlayer().getUuid(),
                event.getMessage()
        )).exceptionally(ex -> {
            //todo what to do on exception?
            return null;
        });
    }

    private void handleCommandEvent(PlayerCommandEvent event) {
        ChatMessage message = new ChatMessage(
                Instant.now(),
                "test-server",
                COMMAND_CHANNEL,
                event.getPlayer().getUuid(),
                event.getCommand()
        );
    }
}
