package net.hollowcube.mql.runtime;

import org.jetbrains.annotations.NotNull;
import net.hollowcube.mql.value.MqlValue;

public interface MqlScope {

    @NotNull MqlValue get(@NotNull String name);

    //todo setter as well i guess

}
