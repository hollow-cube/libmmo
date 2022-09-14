package net.hollowcube;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Env {
    /**
     * Strict mode is enabled in production, but may be disabled during tests.
     * <p>
     * It should be used to check cases which are fine during development but are a fatal problem in production. For
     * example, if a registry is empty for any reason in production the server should not be allowed to start.
     */
    public static final Boolean STRICT_MODE = Boolean.valueOf(System.getProperty("unnamed.strict", "false"));


    private static final Logger STRICT_LOGGER = LoggerFactory.getLogger("STRICT");

    public static void strictValidation(@NotNull String message, @NotNull Supplier<Boolean> predicate) {
        if (STRICT_MODE && predicate.get()) {
            STRICT_LOGGER.error(message);
            System.exit(1);
        }
    }

}
