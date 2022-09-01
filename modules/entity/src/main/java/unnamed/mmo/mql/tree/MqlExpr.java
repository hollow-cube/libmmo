package unnamed.mmo.mql.tree;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.value.MqlValue;

public sealed interface MqlExpr permits MqlAccessExpr, MqlBinaryExpr, MqlNumberExpr, MqlIdentExpr {

    MqlValue evaluate(@NotNull MqlScope scope);

    <P, R> R visit(@NotNull MqlVisitor<P, R> visitor, P p);
}
