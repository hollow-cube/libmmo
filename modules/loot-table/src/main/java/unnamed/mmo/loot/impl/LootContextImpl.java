package unnamed.mmo.loot.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.loot.LootContext;

import java.util.HashMap;
import java.util.Map;

public record LootContextImpl(
        @NotNull Map<String, Object> dataMap,
        @NotNull NumberSource numbers
) implements LootContext {

    public LootContextImpl {
        dataMap = Map.copyOf(dataMap);
    }

    @Override
    public double random() {
        return numbers().random();
    }

    @Override
    public <T> @Nullable T get(@NotNull Key<T> key) {
        final Object data = dataMap.get(key.name());
        if (data == null || !key.type().isAssignableFrom(data.getClass()))
            return null;
        //noinspection unchecked
        return (T) data;
    }
}
