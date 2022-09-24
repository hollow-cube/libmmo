package net.hollowcube.mql.runtime;

import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;

public class MqlScriptScope implements MqlScope {

    private final MqlScope query;
    private final MqlScope.Mutable actor;
    private final MqlScope context;
    private final MqlScope.Mutable temp = new MqlScopeImpl.Mutable();

    public MqlScriptScope(@NotNull MqlScope query, @NotNull Mutable actor, @NotNull MqlScope context) {
        this.query = query;
        this.actor = actor;
        this.context = context;
    }

    @Override
    public @NotNull MqlValue get(@NotNull String name) {
        return switch (name) {
            case "math", "m" -> MqlMath.INSTANCE;
            case "query", "q" -> query;
            case "temp", "t" -> temp;
            case "variable", "v" -> actor;
            case "context", "c" -> context;
            default -> throw new MqlRuntimeError("unknown environment object: " + name);
        };
    }
}
