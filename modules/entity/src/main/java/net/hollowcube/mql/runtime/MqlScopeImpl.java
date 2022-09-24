package net.hollowcube.mql.runtime;

import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MqlScopeImpl implements MqlScope {
    protected final Map<String, MqlValue> data = new HashMap<>();

    @Override
    public @NotNull MqlValue get(@NotNull String name) {
        return data.getOrDefault(name, MqlValue.NULL);
    }


    public static class Mutable extends MqlScopeImpl implements MqlScope.Mutable {

        @Override
        public void set(@NotNull String name, @NotNull MqlValue value) {
            data.put(name, value);
        }

    }

}
