package net.hollowcube.logging;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * SLF4J wrapper with a sane API for MDC.
 * <p>
 * Context variables are important for searching within log tools such as Loki (grafana) and the default usage of MDC is
 * extremely verbose and error-prone.
 * <p>
 * Create using {@link LoggerFactory}.
 */
public interface Logger {

    void debug(@NotNull String message);

    void debug(@NotNull String message, @NotNull Map<String, Object> context);

    void info(@NotNull String message);

    void info(@NotNull String message, @NotNull Map<String, Object> context);

    void warn(@NotNull String message);

    void warn(@NotNull String message, @NotNull Map<String, Object> context);

    void error(@NotNull String message);

    void error(@NotNull String message, @NotNull Map<String, Object> context);

}
