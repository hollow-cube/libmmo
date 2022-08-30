package unnamed.mmo.mql.tree;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.value.MqlHolder;
import unnamed.mmo.mql.value.MqlValue;

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
