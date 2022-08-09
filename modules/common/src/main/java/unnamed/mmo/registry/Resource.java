package unnamed.mmo.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public interface Resource extends Keyed {

    @Contract(pure = true)
    @NotNull NamespaceID namespace();

    @Override
    default @NotNull Key key() {
        return namespace();
    }

    @Contract(pure = true)
    default @NotNull String name() {
        return namespace().asString();
    }


    interface Id extends Resource {

        @Contract(pure = true)
        int id();

    }


    Path DATA_PATH = Path.of(System.getProperty("unnamed.data.dir", "data"));

    /**
     * Attempts to load a resource file with the following priorities
     * <ol>
     *     <li>Read from `jar://data/{name}`</li>
     *     <li>Read from `{DATA_PATH}/{name}`</li>
     *     <li>null</li>
     * </ol>
     *
     * @param name The resource name and extension to load
     * @return The resource file content, or null if missing.
     */
    static @Nullable String load(@NotNull String name) {
        InputStream packaged = Resource.class.getClassLoader().getResourceAsStream("data/" + name);
        if (packaged != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(packaged))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(Resource.class);
                logger.warn("Found resource {} in classpath, but failed to load it", name, e);
            }
        }

        Path external = DATA_PATH.resolve(name);
        if (Files.exists(external) && Files.isRegularFile(external)) {
            try (BufferedReader reader = Files.newBufferedReader(external)) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(Resource.class);
                logger.warn("Found resource {} in external data, but failed to load it", name, e);
            }
        }

        return null;
    }

    /**
     * Loads a resource using the semantics of {@link #load(String)}, but the resource must be a json file with
     * an array at the root. If the file cannot be found, an empty array will be returned.
     */
    static @NotNull JsonArray loadJsonArray(@NotNull String resource) {
        String content = load(resource);
        if (content == null)
            return new JsonArray();

        return JsonParser.parseString(content).getAsJsonArray();
    }
}
