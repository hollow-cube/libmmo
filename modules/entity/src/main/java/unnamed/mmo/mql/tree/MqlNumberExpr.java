package unnamed.mmo.mql.tree;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.value.MqlNumberValue;
import unnamed.mmo.mql.value.MqlValue;

public record MqlNumberExpr(@NotNull MqlNumberValue value) implements MqlExpr {

    public MqlNumberExpr(double value) {
        this(new MqlNumberValue(value));
    }

    @Override
    public MqlValue evaluate(@NotNull MqlScope scope) {
        return value;
    }

    @Override
    public <P, R> R visit(@NotNull MqlVisitor<P, R> visitor, P p) {
        return visitor.visitNumberExpr(this, p);
    }
}
