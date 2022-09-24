package net.hollowcube.mql.value;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public non-sealed interface MqlCallable extends MqlValue {

    /** Returns the arity of the function, or -1 if it is variadic/otherwise unknown */
    default int arity() { return -1; }

    @NotNull MqlValue call(@NotNull List<MqlValue> args);

}
