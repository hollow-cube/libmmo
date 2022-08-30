package unnamed.mmo.mql.tree;

import org.jetbrains.annotations.NotNull;

public class MqlPrinter implements MqlVisitor<Void, String> {

    @Override
    public String visitBinaryExpr(@NotNull MqlBinaryExpr expr, Void unused) {
        return String.format(
                "(%s %s %s)",
                switch (expr.operator()) {
                    case PLUS -> "+";
                },
                visit(expr.lhs(), null),
                visit(expr.rhs(), null)
        );
    }

    @Override
    public String visitAccessExpr(@NotNull MqlAccessExpr expr, Void unused) {
        return String.format(
                "(. %s %s)",
                visit(expr.lhs(), null),
                expr.target()
        );
    }

    @Override
    public String visitNumberExpr(@NotNull MqlNumberExpr expr, Void unused) {
        return String.valueOf(expr.value());
    }

    @Override
    public String visitRefExpr(@NotNull MqlIdentExpr expr, Void unused) {
        return expr.value();
    }

    @Override
    public String defaultValue() {
        return "##Error";
    }
}
