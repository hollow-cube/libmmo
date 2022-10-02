package net.hollowcube.blocks.schem;

import net.hollowcube.registry.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchematicManager {
    private static final Path BASE_DIR = Resource.DATA_PATH.resolve("schem");
    private static final Map<String, Schematic> cache = new ConcurrentHashMap<>();

    public static @UnknownNullability Schematic get(@NotNull String name) {
        return cache.computeIfAbsent(name, unused -> {
            try {
                var path = BASE_DIR.resolve(name + ".schem");
                return SchematicReader.read(path);
            } catch (Exception e) {
                //todo better error handling here as well as in the reader
                throw new RuntimeException(e);
            }
        });
    }
}
