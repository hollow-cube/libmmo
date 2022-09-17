package net.hollowcube.mql.tree;

import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlNumberValue;
import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;

public record MqlBinaryExpr(
        @NotNull Op operator,
        @NotNull MqlExpr lhs,
        @NotNull MqlExpr rhs
) implements MqlExpr {
    public enum Op {
        PLUS
    }

    @Override
    public MqlValue evaluate(@NotNull MqlScope scope) {
        var lhs = lhs().evaluate(scope).cast(MqlNumberValue.class);
        var rhs = lhs().evaluate(scope).cast(MqlNumberValue.class);

        return switch (operator()) {
            case PLUS -> new MqlNumberValue(lhs.value() + rhs.value());
        };
    }

    @Override
    public <P, R> R visit(@NotNull MqlVisitor<P, R> visitor, P p) {
        return visitor.visitBinaryExpr(this, p);
    }
}
