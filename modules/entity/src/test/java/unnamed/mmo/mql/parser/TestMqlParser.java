package unnamed.mmo.mql.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import unnamed.mmo.mql.tree.MqlPrinter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMqlParser {

    @MethodSource("inputPairs")
    @ParameterizedTest(name = "{0}")
    public void testInputPairs(String name, String input, String expected) {
        var expr = new MqlParser(input).parse();
        var actual = new MqlPrinter().visit(expr, null);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> inputPairs() {
        return Stream.of(
                Arguments.of("basic number",
                        "1", "1.0"),
                Arguments.of("basic ref",
                        "abc", "abc"),
                Arguments.of("basic add",
                        "1 + 2", "(+ 1.0 2.0)"),
                Arguments.of("nested add",
                        "1 + 2 + 3", "(+ (+ 1.0 2.0) 3.0)"),
                Arguments.of("basic access",
                        "a.b", "(. a b)"),
                Arguments.of("access/add precedence",
                        "a.b + 1", "(+ (. a b) 1.0)")
        );
    }
}
