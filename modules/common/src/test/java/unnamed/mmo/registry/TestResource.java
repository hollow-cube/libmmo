package unnamed.mmo.registry;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public record TestResource(
        @NotNull NamespaceID namespace,
        @NotNull String string
) implements Resource {
    public static final Resource.Type RESOURCE_TYPE = new Resource.Type("test");

}
