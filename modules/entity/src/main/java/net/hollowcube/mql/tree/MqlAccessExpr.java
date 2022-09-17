package net.hollowcube.mql.tree;

import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlHolder;
import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;

public record MqlAccessExpr(
        @NotNull MqlExpr lhs,
        @NotNull String target
) implements MqlExpr {

    @Override
    public MqlValue evaluate(@NotNull MqlScope scope) {
        var lhs = lhs().evaluate(scope).cast(MqlHolder.class);
        return lhs.get(target());
    }

    @Override
    public <P, R> R visit(@NotNull MqlVisitor<P, R> visitor, P p) {
        return visitor.visitAccessExpr(this, p);
    }
}
