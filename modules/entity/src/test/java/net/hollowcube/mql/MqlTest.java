package net.hollowcube.mql;

import net.hollowcube.mql.parser.MqlParser;
import net.hollowcube.mql.runtime.MqlRuntimeError;
import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlHolder;
import net.hollowcube.mql.value.MqlNumberValue;
import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MqlTest {

    @Test
    public void basicQueryCall() {
        var source = "q.is_alive";
        var expr = new MqlParser(source).parse();

        var scope = new MqlScope() {
            @Override
            public @NotNull MqlValue get(@NotNull String name) {
                if (!name.equals("q") && !name.equals("query"))
                    throw new MqlRuntimeError("unknown environment object: " + name);
                return (MqlHolder) queryFunction -> switch (queryFunction) {
                    case "is_alive" -> new MqlNumberValue(1);
                    default -> throw new MqlRuntimeError("no such query function: " + queryFunction);
                };
            }
        };
        var result = expr.evaluate(scope);
        assertTrue(result instanceof MqlNumberValue);
        assertEquals(1, ((MqlNumberValue) result).value());
    }

}
