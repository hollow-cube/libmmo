package net.hollowcube.mql.tree;

import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlNumberValue;
import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;

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
