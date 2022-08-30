package unnamed.mmo.mql.value;

import org.jetbrains.annotations.NotNull;

public non-sealed interface MqlHolder extends MqlValue {

    @NotNull MqlValue get(@NotNull String name);

}
