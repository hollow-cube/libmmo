package net.hollowcube.mql.value;

import net.hollowcube.mql.runtime.MqlRuntimeError;
import org.jetbrains.annotations.NotNull;

/**
 * Mutable marker for any possible mql value.
 */
public sealed interface MqlValue permits MqlCallable, MqlHolder, MqlIdentValue, MqlNumberValue {
    MqlValue NULL = new MqlNumberValue(0.0);

    static @NotNull MqlValue from(boolean bool) {
        return new MqlNumberValue(bool ? 1 : 0);
    }

    static @NotNull MqlValue from(double dbl) {
        return new MqlNumberValue(dbl);
    }

    default <Target> Target cast(@NotNull Class<Target> targetType) {
        if (targetType.isInstance(this))
            //noinspection unchecked
            return (Target) this;
        throw new MqlRuntimeError("cannot cast " + this.getClass().getSimpleName() + " to " + targetType.getSimpleName());
    }

}
