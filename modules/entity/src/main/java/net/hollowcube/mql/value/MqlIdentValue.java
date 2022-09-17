package net.hollowcube.mql.value;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record MqlIdentValue(@NotNull String value) implements MqlValue {
}
