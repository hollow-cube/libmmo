package net.hollowcube.chat.storage;

import net.hollowcube.chat.ChatMessage;
import net.hollowcube.chat.ChatQuery;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public record MockChatStorage(
        @NotNull List<ChatMessage> messages,
        @NotNull List<ChatQuery> queries
) implements ChatStorage {

    public MockChatStorage() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public CompletableFuture<Void> recordChatMessage(@NotNull ChatMessage message) {
        messages.add(message);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<ChatMessage>> queryChatMessages(@NotNull ChatQuery query) {
        queries.add(query);
        return CompletableFuture.completedFuture(List.of());
    }

    public ChatMessage assertOneMessage() {
        assertEquals(1, messages.size());
        return messages.get(0);
    }

    public void assertEmpty() {
        assertEquals(0, messages.size());
    }
}
