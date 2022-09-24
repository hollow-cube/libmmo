package net.hollowcube.registry;

import org.jetbrains.annotations.NotNull;

public class MissingEntryException extends RuntimeException{
    private final @NotNull Registry<?> registry;
    private final @NotNull String key;

    public MissingEntryException(@NotNull Registry<?> registry, @NotNull String key) {
        super("Missing registry entry: " + key + " in " + registry + "!");
        this.registry = registry;
        this.key = key;
    }

    public @NotNull Registry<?> registry() {
        return registry;
    }

    public @NotNull String key() {
        return key;
    }
}
