package unnamed.mmo.loot.context;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ContextKey<T>(
        @NotNull String name,
        @NotNull Class<T> type
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextKey<?> that = (ContextKey<?>) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
