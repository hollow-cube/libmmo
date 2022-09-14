package net.hollowcube.logging;

import org.jetbrains.annotations.NotNull;

public final class LoggerFactory {
    private LoggerFactory() {}

    public static @NotNull Logger getLogger(@NotNull Class<?> clazz) {
        return new LoggerImpl(org.slf4j.LoggerFactory.getLogger(clazz));
    }

}
