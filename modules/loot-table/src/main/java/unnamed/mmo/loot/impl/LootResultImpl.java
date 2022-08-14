package unnamed.mmo.loot.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import unnamed.mmo.loot.LootResult;
import unnamed.mmo.loot.LootContext;
import unnamed.mmo.registry.Registry;

import java.util.*;

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
    public <T> void override(@NotNull Class<T> type, @NotNull Distributor<T> distributor) {
        overrides.put(type, distributor);
    }

    @Override
    public void apply(@NotNull LootContext context) {
        for (Object result : results) {
            //noinspection unchecked
            Distributor<Object> distributor = (Distributor<Object>) findDistributor(result.getClass());
            if (distributor == null) {
                //todo better way to handle this?
                throw new RuntimeException("No distributor for type " + result.getClass());
            }

            distributor.apply(context, result);
        }
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
