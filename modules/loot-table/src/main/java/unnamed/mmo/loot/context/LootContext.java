package unnamed.mmo.loot.context;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.data.NumberSource;

import java.util.HashMap;
import java.util.Map;

public interface LootContext extends NumberSource {

    static @NotNull Builder builder(@NotNull String source) {
        return new Builder()
                .key(ContextKeys.SOURCE_NAME, source);
    }

    <T> @Nullable T get(@NotNull ContextKey<T> key);

    default <T> @NotNull T getOrDefault(@NotNull ContextKey<T> key, @NotNull T defaultValue) {
        final T value = get(key);
        return value != null ? value : defaultValue;
    }


    class Builder {
        private final Map<String, Object> dataMap = new HashMap<>();
        private NumberSource numbers = NumberSource.threadLocalRandom();


        @Contract("_, _ -> this")
        public <T> @NotNull Builder key(@NotNull ContextKey<T> key, @NotNull T value) {
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
            //todo this is gross, for sure the wrong way to write a builder :P
            return new LootContext() {
                @Override
                public <T> @Nullable T get(@NotNull ContextKey<T> key) {
                    final Object data = dataMap.get(key.name());
                    if (data == null || !key.type().isAssignableFrom(data.getClass()))
                        return null;
                    //noinspection unchecked
                    return (T) data;
                }

                @Override
                public double random() {
                    return numbers.random();
                }
            };
        }

    }

}
