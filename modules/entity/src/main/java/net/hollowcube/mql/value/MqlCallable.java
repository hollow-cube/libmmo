package net.hollowcube.mql.value;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public non-sealed interface MqlCallable extends MqlValue {

    @NotNull MqlValue call(@NotNull List<MqlValue> args);

}
