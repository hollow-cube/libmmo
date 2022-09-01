package unnamed.mmo.mql;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.mql.parser.MqlParser;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.tree.MqlExpr;
import unnamed.mmo.mql.tree.MqlNumberExpr;
import unnamed.mmo.mql.value.MqlNumberValue;
import unnamed.mmo.mql.value.MqlValue;
import unnamed.mmo.util.DFUUtil;

public record MqlScript(@NotNull MqlExpr expr) {

    public static final Codec<MqlScript> CODEC = Codec.either(Codec.STRING, Codec.STRING.listOf())
            .xmap(either -> DFUUtil.value(either.mapRight(lines -> String.join("\n", lines))), Either::left)
            //todo lossless parser
            .xmap(MqlScript::parse, unused -> {throw new RuntimeException("cannot serialize an mql script");});

    public static @NotNull MqlScript parse(@NotNull String script) {
        if (script.trim().isEmpty()) // Empty script is always the number 1
            return new MqlScript(new MqlNumberExpr(1));
        MqlExpr expr = new MqlParser(script).parse();
        return new MqlScript(expr);
    }

    public double evaluate(@NotNull MqlScope scope) {
        MqlValue result = expr().evaluate(scope);
        if (result instanceof MqlNumberValue num)
            return num.value();
        return 0.0;
    }

    public boolean evaluateToBool(@NotNull MqlScope scope) {
        MqlValue result = expr().evaluate(scope);
        if (result instanceof MqlNumberValue num)
            return num.value() != 0;
        return result != MqlValue.NULL;
    }

}
