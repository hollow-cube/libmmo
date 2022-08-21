package unnamed.mmo.loot.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import unnamed.mmo.loot.LootContext;
import unnamed.mmo.loot.LootResult;
import unnamed.mmo.registry.Registry;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class LootResultImpl implements LootResult {
    private static final Registry<DefaultDistributor<?>> DISTRIBUTOR_REGISTRY = Registry.manual("loot_distributors", () -> {
        List<DefaultDistributor<?>> registry = new ArrayList<>();
        for (DefaultDistributor<?> entry : ServiceLoader.load(DefaultDistributor.class))
            registry.add(entry);
        return registry;
    });

    private final Map<Class<?>, Distributor<?>> overrides = new HashMap<>();
    private final List<Object> results;

    public LootResultImpl(List<Object> results) {
        this.results = results;
    }


    @Override
    public @NotNull Collection<Object> results() {
        return results;
    }

    @Override
    public int size() {
        return results.size();
    }

    @Override
    public <T> void override(@NotNull Class<T> type, @NotNull Distributor<T> distributor) {
        overrides.put(type, distributor);
    }

    @Override
    public @NotNull CompletableFuture<Void> apply(@NotNull LootContext context) {
        var tasks = new CompletableFuture[size()];
        for (int i = 0; i < tasks.length; i++) {
            final Object result = results.get(i);

            //noinspection unchecked
            Distributor<Object> distributor = (Distributor<Object>) findDistributor(result.getClass());
            if (distributor == null) {
                //todo better way to handle this?
                throw new RuntimeException("No distributor for type " + result.getClass());
            }

            tasks[i] = distributor.apply(context, result);
        }

        return CompletableFuture.allOf(tasks);
    }

    @SuppressWarnings("unchecked")
    private <T> @UnknownNullability Distributor<T> findDistributor(Class<T> type) {
        for (var entry : overrides.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (Distributor<T>) entry.getValue();
            }
        }

        for (var entry : DISTRIBUTOR_REGISTRY.values()) {
            if (entry.type().isAssignableFrom(type)) {
                return (Distributor<T>) entry;
            }
        }

        return null;
    }
}
