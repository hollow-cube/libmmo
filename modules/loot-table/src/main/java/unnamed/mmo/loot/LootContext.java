package unnamed.mmo.loot;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.loot.impl.LootContextImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface LootContext extends NumberSource {

    // Required keys

    /** The source of the loot table generation, eg "mining", "foraging". */
    LootContext.Key<String> SOURCE_NAME = new LootContext.Key<>("source_name", String.class);

    /** The target of the loot generation, if it is an entity. */
    LootContext.Key<Entity> THIS_ENTITY = new LootContext.Key<>("this", Entity.class);

    // Common hints

    /** The location of the loot generation. */
    LootContext.Key<Point> POSITION = new LootContext.Key<>("position", Point.class);
    /** The direction the loot should be generated in. */
    LootContext.Key<Vec> DIRECTION = new LootContext.Key<>("direction", Vec.class);


    static @NotNull Builder builder(@NotNull String source) {
        return new Builder()
                .key(SOURCE_NAME, source);
    }

    <T> @Nullable T get(@NotNull LootContext.Key<T> key);

    default <T> @NotNull T getOrDefault(@NotNull LootContext.Key<T> key, @NotNull T defaultValue) {
        final T value = get(key);
        return value != null ? value : defaultValue;
    }


    record Key<T>(
            @NotNull String name,
            @NotNull Class<T> type
    ) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key<?> that)) return false;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }


    class Builder {
        private final Map<String, Object> dataMap = new HashMap<>();
        private NumberSource numbers = NumberSource.threadLocalRandom();


        @Contract("_, _ -> this")
        public <T> @NotNull Builder key(@NotNull LootContext.Key<T> key, @NotNull T value) {
            dataMap.put(key.name(), value);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder numbers(@NotNull NumberSource numbers) {
            this.numbers = numbers;
            return this;
        }

        @Contract("-> new")
        public LootContext build() {
            return new LootContextImpl(dataMap, numbers);
        }

    }
}
