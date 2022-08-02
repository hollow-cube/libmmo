package unnamed.mmo.skill.mining;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

record OreImpl(
        @NotNull OreRegistry.Entry registry
) implements Ore {


    @Override
    public @NotNull NamespaceID namespace() {
        return registry().namespace();
    }

    @Override
    public int health() {
        return registry().health();
    }


    // Static helpers

    static Ore get(@NotNull String namespaceId) {
        return OreRegistry.CONTAINER.get(namespaceId);
    }

    static Ore getSafe(@NotNull String namespace) {
        return OreRegistry.CONTAINER.getSafe(namespace);
    }

    static Collection<Ore> values() {
        return OreRegistry.CONTAINER.values();
    }


}
