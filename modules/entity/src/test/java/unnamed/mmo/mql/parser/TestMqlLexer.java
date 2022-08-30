package unnamed.mmo.mql.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestMqlLexer {

    @ParameterizedTest
    @MethodSource("individualSymbols")
    public void testIndividualSymbols(String input, MqlToken.Type expected) {
        var lexer = new MqlLexer(input);

        var token = lexer.next();
        assertNotNull(token);
        assertEquals(expected, token.type());

        var eof = lexer.next();
        assertNull(eof);
    }

    private static Stream<Arguments> individualSymbols() {
        return Stream.of(
                Arguments.of("+", MqlToken.Type.PLUS),
                Arguments.of(".", MqlToken.Type.DOT),

                Arguments.of("123", MqlToken.Type.NUMBER),
                Arguments.of("123.", MqlToken.Type.NUMBER),
                Arguments.of("123.456", MqlToken.Type.NUMBER),

                Arguments.of("abc", MqlToken.Type.IDENT),
                Arguments.of("aBc", MqlToken.Type.IDENT),
                Arguments.of("aBc1", MqlToken.Type.IDENT)
        );
    }

}
