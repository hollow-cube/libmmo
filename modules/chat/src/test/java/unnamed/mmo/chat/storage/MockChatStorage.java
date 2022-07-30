package unnamed.mmo.chat.storage;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.chat.ChatMessage;
import unnamed.mmo.chat.ChatQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public record MockChatStorage(@NotNull List<ChatMessage> messages) implements ChatStorage {

    public MockChatStorage() {
        this(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Void> recordChatMessage(@NotNull ChatMessage message) {
        messages.add(message);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<ChatMessage>> queryChatMessages(@NotNull ChatQuery query) {
        return null;
    }

    public ChatMessage assertOneMessage() {
        assertEquals(1, messages.size());
        return messages.get(0);
    }

    public void assertEmpty() {
        assertEquals(0, messages.size());
    }
}
