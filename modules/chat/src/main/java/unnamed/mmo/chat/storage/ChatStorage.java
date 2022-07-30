package unnamed.mmo.chat.storage;

import com.mongodb.client.MongoClient;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.chat.ChatMessage;
import unnamed.mmo.chat.ChatQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatStorage {
    static @NotNull ChatStorage noop() {
        return new NoopChatStorage();
    }

    static @NotNull ChatStorage mongo(MongoClient client) {
        return new MongoChatStorage(client);
    }


    /**
     * Record a chat message to storage. How/when/if the message is written is up to the
     * implementing class, but may not block the current thread to do so.
     *
     * @param message The chat message to save
     */
    CompletableFuture<Void> recordChatMessage(@NotNull ChatMessage message);

    CompletableFuture<List<ChatMessage>> queryChatMessages(@NotNull ChatQuery query);

}
