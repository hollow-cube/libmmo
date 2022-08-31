package unnamed.mmo.quest.objective;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ObjectiveData(
        @NotNull Map<String, ObjectiveData> children,
        @NotNull String data
) {
}
