package unnamed.mmo.quest.storage;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface QuestStorage {

    static @NotNull QuestStorage memory() {
        return new MemoryQuestStorage();
    }

    @NotNull CompletableFuture<QuestData> readQuestData(@NotNull UUID playerId);

    @NotNull CompletableFuture<Void> saveQuestData(@NotNull QuestData data);

}
