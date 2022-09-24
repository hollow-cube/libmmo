package net.hollowcube.mql.runtime;

import net.hollowcube.mql.value.MqlHolder;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.mql.value.MqlValue;

public interface MqlScope extends MqlHolder {

    MqlScope EMPTY = unused -> MqlValue.NULL;

    @NotNull MqlValue get(@NotNull String name);

    interface Mutable extends MqlScope {

        void set(@NotNull String name, @NotNull MqlValue value);

    }

}
