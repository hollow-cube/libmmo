package unnamed.mmo.chat.storage;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.chat.ChatMessage;
import unnamed.mmo.chat.ChatQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Dummy chat storage which does not do anything with the messages.
 */
class NoopChatStorage implements ChatStorage {

    @Override
    public CompletableFuture<Void> recordChatMessage(@NotNull ChatMessage message) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<ChatMessage>> queryChatMessages(@NotNull ChatQuery query) {
        return CompletableFuture.completedFuture(List.of());
    }
}
