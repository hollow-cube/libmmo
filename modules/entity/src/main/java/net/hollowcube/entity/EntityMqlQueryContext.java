package net.hollowcube.entity;

import net.hollowcube.mql.runtime.MqlRuntimeError;
import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlHolder;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.mql.value.MqlValue;

//todo this whole class sucks, need to work out mql querying better
public record EntityMqlQueryContext(@NotNull UnnamedEntity entity) implements MqlScope {
    @Override
    public @NotNull MqlValue get(@NotNull String name) {
        if (!name.equals("q") && !name.equals("query"))
            throw new MqlRuntimeError("unknown environment object: " + name);
        return (MqlHolder) queryFunction -> switch (queryFunction) {
            // Entity
            case "is_alive" -> MqlValue.from(!entity.isDead());
            case "has_target" -> MqlValue.from(entity.brain().getTarget() != null);
            // World
            case "time_of_day" -> MqlValue.from(entity.getInstance().getTime() / 18_000D);
            default -> throw new MqlRuntimeError("no such query function: " + queryFunction);
        };
    }
}
