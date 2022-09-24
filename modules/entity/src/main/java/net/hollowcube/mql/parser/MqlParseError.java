package net.hollowcube.mql.parser;

import org.jetbrains.annotations.NotNull;

public class MqlParseError extends RuntimeException {

    public MqlParseError(@NotNull String message) {
        super(message);
    }

}
