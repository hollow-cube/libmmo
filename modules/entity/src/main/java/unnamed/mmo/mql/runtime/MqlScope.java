package unnamed.mmo.mql.runtime;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.value.MqlValue;

public interface MqlScope {

    @NotNull MqlValue get(@NotNull String name);

    //todo setter as well i guess

}
