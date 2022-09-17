package net.hollowcube.mql.runtime;

import org.jetbrains.annotations.NotNull;

public class MqlRuntimeError extends RuntimeException {
    public MqlRuntimeError(@NotNull String message) {
        super(message);
    }
}
