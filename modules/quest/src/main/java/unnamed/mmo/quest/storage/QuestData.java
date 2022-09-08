package unnamed.mmo.quest.storage;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.objective.ObjectiveData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record QuestData(
        @NotNull UUID playerId,
        @NotNull List<String> completed,
        @NotNull Map<String, ObjectiveData> inProgress
) {

}
