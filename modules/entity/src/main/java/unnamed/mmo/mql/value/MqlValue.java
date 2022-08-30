package unnamed.mmo.mql.value;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.runtime.MqlRuntimeError;

/**
 * Mutable marker for any possible mql value.
 */
public sealed interface MqlValue permits MqlCallable, MqlHolder, MqlIdentValue, MqlNull, MqlNumberValue {
    MqlValue NULL = MqlNull.INSTANCE;

    default <Target> Target cast(@NotNull Class<Target> targetType) {
        if (targetType.isInstance(this))
            //noinspection unchecked
            return (Target) this;
        throw new MqlRuntimeError("cannot cast " + this.getClass().getSimpleName() + " to " + targetType.getSimpleName());
    }

}
