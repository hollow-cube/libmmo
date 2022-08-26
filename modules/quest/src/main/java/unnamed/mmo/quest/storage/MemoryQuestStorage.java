package unnamed.mmo.quest.storage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

class MemoryQuestStorage implements QuestStorage {
    private final Map<UUID, QuestData> data = new HashMap<>();

    @Override
    public @NotNull CompletableFuture<QuestData> readQuestData(@NotNull UUID playerId) {
        final QuestData data = this.data.getOrDefault(playerId, new QuestData(playerId, List.of(), Map.of()));
        return CompletableFuture.completedFuture(data);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveQuestData(@NotNull QuestData data) {
        this.data.put(data.playerId(), data);
        return CompletableFuture.completedFuture(null);
    }

}
