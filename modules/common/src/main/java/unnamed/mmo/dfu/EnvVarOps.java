package unnamed.mmo.dfu;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import kotlin.NotImplementedError;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class EnvVarOps implements DynamicOps<String> {
    public static final EnvVarOps DOTENV = new EnvVarOps() {
        private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        @Override
        protected Collection<String> envKeys() {
            return dotenv.entries().stream().map(DotenvEntry::getKey).toList();
        }

        @Override
        protected String get(String path) {
            if (path.isEmpty())
                return "";
            return dotenv.get(path);
        }
    };

    @Override
    public String empty() {
        return "";
    }

    @Override
    public DataResult<Number> getNumberValue(String input) {
        String value = get(input);

        // This is really primitive number parsing, but I guess it will work.
        try {
            if (value.contains(".")) {
                return DataResult.success(Double.parseDouble(value));
            } else {
                return DataResult.success(Long.parseLong(value));
            }
        } catch (NumberFormatException e) {
            return DataResult.error(e.getMessage());
        }
    }

    @Override
    public DataResult<Boolean> getBooleanValue(String input) {
        String value = get(input).toLowerCase(Locale.ROOT);
        // Do not use Boolean.parseBoolean because it treats invalid as false.
        if (value.equals("true"))
            return DataResult.success(true);
        if (value.equals("false"))
            return DataResult.success(false);
        return DataResult.error("Not a boolean: " + input);
    }

    @Override
    public DataResult<String> getStringValue(String input) {
        return DataResult.success(get(input));
    }

    @Override
    public DataResult<MapLike<String>> getMap(String input) {
        // All the available keys starting with the input prefix
        Set<String> possibleKeys = new HashSet<>();
        for (String path : envKeys()) {
            if (path.toUpperCase(Locale.ROOT).startsWith(input)) {
                possibleKeys.add(path);
            }
        }

        return DataResult.success(new MapLike<>() {
            @Override
            public String get(String key) {
                // Create the new path by appending to the original query path
                String newPath = key.toUpperCase(Locale.ROOT);
                if (!input.isEmpty()) {
                    newPath = input + "_" + newPath;
                }

                // The map contains any path starting with this one, so we need to iterate over it.
                for (var entryKey : possibleKeys) {
                    if (entryKey.startsWith(newPath)) {
                        // Return the new path. entryKey contains the entire path
                        return newPath;
                    }
                }

                return null;
            }

            @Override
            public Stream<Pair<String, String>> entries() {
                return notImplemented("getMap -> entries");
            }
        });
    }

    @Override
    public DataResult<Consumer<Consumer<String>>> getList(String input) {
        // Children contains every path which starts with input and is followed by a single number segment
        Map<Integer, String> children = new HashMap<>(); // (map to ignore duplicate keys)
        for (String path : envKeys()) {
            if (!path.toUpperCase(Locale.ROOT).startsWith(input))
                continue;
            String[] rest = path.substring(input.length()).split("_");
            // First entry must be "" because we split on _00_REST
            if (rest.length < 2 || !rest[0].isEmpty())
                continue;
            // Try to parse number segment
            int index = Integer.parseInt(rest[1]);
            children.put(index, input + "_" + index);
        }

        // Sort on index
        String[] sortedChildren = new String[children.size()];
        for (var child : children.entrySet()) {
            // If the child is not within the sorted range then there was an order problem in the original (eg skipping a number)
            if (child.getKey() < 0 || child.getKey() >= children.size())
                return DataResult.error(String.format("invalid index of %s: %s", input, child));
            sortedChildren[child.getKey()] = child.getValue();
        }

        return DataResult.success(c -> {
            for (var child : sortedChildren) {
                Check.notNull(child, "missing list element");
                c.accept(child);
            }
        });
    }

    // NOT IMPLEMENTED BELOW
    // The rest of DynamicOps is not implemented. The reasons are:
    // - Serialization is not supported by EnvVarOps.
    // - I have not found a use for the function. If we find one it will be implemented.

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, String input) {
        //todo
        throw new NotImplementedError("convertTo");
    }

    @Override
    public String createNumeric(Number i) {
        return notImplemented("createNumeric");
    }

    @Override
    public String createBoolean(boolean value) {
        return notImplemented("createBoolean");
    }

    @Override
    public String createString(String value) {
        return notImplemented("createString");
    }

    @Override
    public DataResult<String> mergeToList(String list, String value) {
        throw new NotImplementedError("mergeToList");
    }

    @Override
    public DataResult<String> mergeToMap(String map, String key, String value) {
        return notImplemented("mergeToMap@3");
    }

    @Override
    public DataResult<String> mergeToMap(String map, MapLike<String> values) {
        throw new NotImplementedError("mergeToMap@2");
    }

    @Override
    public DataResult<Stream<Pair<String, String>>> getMapValues(String input) {
        throw new NotImplementedError("getMapValues");
    }

    @Override
    public DataResult<Consumer<BiConsumer<String, String>>> getMapEntries(String input) {
        throw new NotImplementedError("getMapEntries");
    }

    @Override
    public String createMap(Stream<Pair<String, String>> map) {
        return notImplemented("createMap");
    }

    @Override
    public DataResult<Stream<String>> getStream(String input) {
        return notImplemented("getStream");
    }

    @Override
    public String createList(Stream<String> input) {
        return notImplemented("createList");
    }

    @Override
    public String remove(String input, String key) {
        return notImplemented("remove");
    }


    protected abstract Collection<String> envKeys();

    protected abstract String get(String path);

    @Contract("_ -> fail")
    private <T> T notImplemented(@NotNull String name) {
        throw new NotImplementedError("EnvVarOps#" + name);
    }
}
