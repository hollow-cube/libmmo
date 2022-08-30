package unnamed.mmo.mql.tree;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.value.MqlNumberValue;
import unnamed.mmo.mql.value.MqlValue;

public sealed interface MqlExpr permits MqlAccessExpr, MqlBinaryExpr, MqlNumberExpr, MqlIdentExpr {

    MqlValue evaluate(@NotNull MqlScope scope);

    default double evaluateToNumber(@NotNull MqlScope scope) {
        MqlValue result = evaluate(scope);
        if (result instanceof MqlNumberValue num)
            return num.value();
        return 0.0;
    }

    default boolean evaluateToBool(@NotNull MqlScope scope) {
        MqlValue result = evaluate(scope);
        if (result instanceof MqlNumberValue num)
            return num.value() != 0;
        return result != MqlValue.NULL;
    }

    <P, R> R visit(@NotNull MqlVisitor<P, R> visitor, P p);
}
