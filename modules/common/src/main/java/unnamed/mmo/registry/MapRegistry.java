package unnamed.mmo.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class MapRegistry<T extends Resource> implements Registry<T> {
    private final Map<String, T> delegate;

    public MapRegistry(Map<String, T> resources) {
        this.delegate = resources;
    }

    @Override
    public @Nullable T getRaw(String namespace) {
        return delegate.get(namespace);
    }

    @Override
    public @NotNull Collection<T> values() {
        return delegate.values();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public @NotNull <K> Index<K, T> index(Function<T, K> mapper) {
        Map<K, T> index = values().stream().collect(Collectors.toMap(mapper, i -> i));
        return new MapIndex<>(index);
    }


    static class MapIndex<K, T extends Resource> implements Index<K, T> {
        private final Map<K, T> delegate;

        MapIndex(Map<K, T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public @UnknownNullability T get(K key) {
            return delegate.get(key);
        }
    }
}
