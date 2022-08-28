package unnamed.mmo.logging;

import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

import java.util.Map;

record LoggerImpl(
        org.slf4j.Logger delegate
) implements Logger {

    public void debug(@NotNull String message) {
        debug(message, Map.of());
    }

    public void debug(@NotNull String message, @NotNull Map<String, Object> context) {
        if (!delegate.isDebugEnabled()) return;

        for (var entry : context.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        delegate.debug(message);

        for (var key : context.keySet()) {
            remove(key);
        }
    }


    public void info(@NotNull String message) {
        info(message, Map.of());
    }

    public void info(@NotNull String message, @NotNull Map<String, Object> context) {
        if (!delegate.isInfoEnabled()) return;

        for (var entry : context.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        delegate.info(message);

        for (var key : context.keySet()) {
            remove(key);
        }
    }

    public void warn(@NotNull String message) {
        warn(message, Map.of());
    }

    public void warn(@NotNull String message, @NotNull Map<String, Object> context) {
        if (!delegate.isWarnEnabled()) return;

        for (var entry : context.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        delegate.warn(message);

        for (var key : context.keySet()) {
            remove(key);
        }
    }

    public void error(@NotNull String message) {
        error(message, Map.of());
    }

    public void error(@NotNull String message, @NotNull Map<String, Object> context) {
        if (!delegate.isErrorEnabled()) return;

        for (var entry : context.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        delegate.error(message);

        for (var key : context.keySet()) {
            remove(key);
        }
    }

    private void put(String key, Object value) {
        MDC.put(key, value == null ? null : value.toString());
    }

    private void remove(String key) {
        MDC.remove(key);
    }
}
