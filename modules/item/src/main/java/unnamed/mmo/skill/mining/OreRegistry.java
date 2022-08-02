package unnamed.mmo.skill.mining;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;

import static net.minestom.server.registry.Registry.Properties;

public class OreRegistry {

    public static final class Entry {
        private final NamespaceID namespace;
        private final int health;

        private Entry(String namespace, Properties props) {
            this.namespace = NamespaceID.from(namespace);
            this.health = props.getInt("health");
        }

        public @NotNull NamespaceID namespace() {
            return namespace;
        }

        public int health() {
            return health;
        }
    }

    private static final Resource.Type RESOURCE = new Resource.Type("ore");

    static final Registry.Container<Ore> CONTAINER = Registry.createContainer(RESOURCE,
            (namespace, props) -> new OreImpl(new Entry(namespace, props)));
}
