package unnamed.mmo.quest.storage;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ObjectiveData(
        @NotNull NamespaceID type,
        @NotNull Map<String, ObjectiveData> children,
        @NotNull String data
) {
}
