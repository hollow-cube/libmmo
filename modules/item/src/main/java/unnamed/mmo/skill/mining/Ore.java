package unnamed.mmo.skill.mining;

import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.registry.Resource;

import java.util.Collection;

public interface Ore extends Resource {
    Tag<Ore> TAG = Tag.String("ore_id").map(Ore::fromNamespaceId, Ore::name);

    int health();


    // Static helpers

    static @NotNull Collection<@NotNull Ore> values() {
        return OreImpl.values();
    }

    static @Nullable Ore fromNamespaceId(@NotNull String namespaceId) {
        return OreImpl.getSafe(namespaceId);
    }

    static @Nullable Ore fromNamespaceId(@NotNull NamespaceID namespaceId) {
        return OreImpl.getSafe(namespaceId.asString());
    }
}
