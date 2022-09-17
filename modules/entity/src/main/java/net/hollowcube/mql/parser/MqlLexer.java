package net.hollowcube.mql.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MqlLexer {
    private final String source;

    private int start = 0;
    private int cursor = 0;

    public MqlLexer(@NotNull String source) {
        this.source = source;
    }

    /**
     * Returns the next token in the input, or null if the end of file was reached.
     * @throws MqlParseError if there is an unexpected token.
     */
    public @Nullable MqlToken next() {
        start = cursor;

        if (atEnd()) return null;

        consumeWhitespace();

        char c = advance();
        if (isAlpha(c))
            return ident();
        if (isDigit(c))
            return number();

        return symbol(c);
    }

    /**
     * Returns the next token <i>without</i> stepping to the next token in the input,
     * or null if the end of file was reached.
     * @throws MqlParseError if there is an unexpected token.
     */
    public @Nullable MqlToken peek() {
        var result = next();
        cursor = start; // Reset to where it was before the call to next.
        return result;
    }

    public @NotNull String span(@NotNull MqlToken token) {
        return source.substring(start, cursor);
    }

    private void consumeWhitespace() {
        while (true) {
            switch (peek0()) {
                case ' ', '\t', '\r', '\n' -> advance();
                default -> {
                    return;
                }
            }
        }
    }

    private MqlToken ident() {
        while (isAlpha(peek0()) || isDigit(peek0())) {
            advance();
        }

        return new MqlToken(MqlToken.Type.IDENT, start, cursor);
    }

    private MqlToken number() {
        // Pre decimal
        while (isDigit(peek0()))
            advance();

        // Decimal, if present
        if (match('.')) {
            while (isDigit(peek0()))
                advance();
        }

        return new MqlToken(MqlToken.Type.NUMBER, start, cursor);
    }

    private MqlToken symbol(char c) {
        var tokenType = switch (c) {
            // @formatter:off
            case '+' -> MqlToken.Type.PLUS;
            case '.' -> MqlToken.Type.DOT;
            default -> throw new MqlParseError(
                    String.format("unexpected token '%s' at %d.", c, cursor));
            // @formatter:on
        };
        return new MqlToken(tokenType, start, cursor);
    }

    private boolean atEnd() {
        return cursor >= source.length();
    }

    private char peek0() {
        if (atEnd())
            return '\u0000';
        return source.charAt(cursor);
    }

    private char advance() {
        if (atEnd()) throw new MqlParseError("unexpected end of input");
        return source.charAt(cursor++);
    }

    private boolean match(char c) {
        if (atEnd()) return false;
        if (peek0() != c) return false;
        advance();
        return true;
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
