package net.hollowcube.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyRegistry<T extends Resource> implements Registry<T> {
    private final Supplier<Registry<T>> registrySupplier;
    private Registry<T> registry = null;

    public LazyRegistry(Supplier<Registry<T>> registrySupplier) {
        this.registrySupplier = registrySupplier;
    }

    @Override
    public @Nullable T getRaw(String namespace) {
        return getRegistry().getRaw(namespace);
    }

    @Override
    public @NotNull Collection<String> keys() {
        return getRegistry().keys();
    }

    @Override
    public @NotNull Collection<T> values() {
        return getRegistry().values();
    }

    @Override
    public int size() {
        return getRegistry().size();
    }

    @Override
    public @NotNull <K> Index<K, T> index(Function<T, K> mapper) {
        return getRegistry().index(mapper);
    }

    private Registry<T> getRegistry() {
        if (registry == null) registry = registrySupplier.get();
        return registry;
    }
}
