package unnamed.mmo.mql.tree;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.value.MqlIdentValue;
import unnamed.mmo.mql.value.MqlValue;

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
