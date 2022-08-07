package unnamed.mmo.registry2;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import unnamed.mmo.registry.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Registry<T extends Resource> {

    // Factory

    static <T extends Resource> Registry<T> create(Supplier<Collection<T>> supplier) {
        Map<String, T> registry = new HashMap<>();
        for (T element : supplier.get()) {
            registry.put(element.name(), element);
        }
        return new MapRegistry<>(registry);
    }


    // Impl

    @Nullable T getRaw(String namespace);

    default @UnknownNullability T get(String namespace) {
        return getRaw(namespace.contains(":") ? namespace : "minecraft:" + namespace);
    }

    default @UnknownNullability T get(NamespaceID namespace) {
        return getRaw(namespace.asString());
    }

    @NotNull Collection<T> values();


    // Derivatives

    interface Index<K, T extends Resource> {

        @UnknownNullability T get(K key);

    }

    @NotNull <K> Index<K, T> index(Function<T, K> mapper);
}
