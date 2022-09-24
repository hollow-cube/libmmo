package net.hollowcube.mql.tree;

import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;

public record MqlIdentExpr(@NotNull String value) implements MqlExpr {

    @Override
    public MqlValue evaluate(@NotNull MqlScope scope) {
        return scope.get(value);
    }

    @Override
    public <P, R> R visit(@NotNull MqlVisitor<P, R> visitor, P p) {
        return visitor.visitRefExpr(this, p);
    }
}
