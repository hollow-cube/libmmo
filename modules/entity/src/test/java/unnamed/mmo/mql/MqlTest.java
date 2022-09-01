package unnamed.mmo.mql;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import unnamed.mmo.mql.parser.MqlParser;
import unnamed.mmo.mql.runtime.MqlRuntimeError;
import unnamed.mmo.mql.runtime.MqlScope;
import unnamed.mmo.mql.value.MqlHolder;
import unnamed.mmo.mql.value.MqlNumberValue;
import unnamed.mmo.mql.value.MqlValue;

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
