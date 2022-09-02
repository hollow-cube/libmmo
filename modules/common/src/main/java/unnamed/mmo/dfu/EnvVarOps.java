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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EnvVarOps implements DynamicOps<EnvVarOps.Path> {
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public record Path(String path) {
        public static final Path EMPTY = new Path("");
    }


    @Override
    public Path empty() {
        return Path.EMPTY;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Path input) {
        //todo
        throw new NotImplementedError("getMapEntries");
    }

    @Override
    public DataResult<Number> getNumberValue(Path input) {
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
    public Path createNumeric(Number i) {
        return illegalSerialization();
    }

    @Override
    public DataResult<Boolean> getBooleanValue(Path input) {
        String value = get(input);
        // Do not use Boolean.parseBoolean because it treats invalid as false.
        if (value.toLowerCase(Locale.ROOT).equals("true"))
            return DataResult.success(true);
        if (value.toLowerCase(Locale.ROOT).equals("false"))
            return DataResult.success(false);
        return DataResult.error("Not a boolean: " + input);
    }

    @Override
    public Path createBoolean(boolean value) {
        return illegalSerialization();
    }

    @Override
    public DataResult<String> getStringValue(Path input) {
        return DataResult.success(get(input));
    }

    @Override
    public Path createString(String value) {
        return illegalSerialization();
    }

    @Override
    public DataResult<Path> mergeToList(Path list, Path value) {
        //todo
        throw new NotImplementedError("getMapEntries");
    }

    @Override
    public DataResult<Path> mergeToMap(Path map, Path key, Path value) {
        return null;
    }

    @Override
    public DataResult<Path> mergeToMap(Path map, MapLike<Path> values) {
        //todo
        throw new NotImplementedError("getMapEntries");
    }

    @Override
    public DataResult<Stream<Pair<Path, Path>>> getMapValues(Path input) {
        //todo
        throw new NotImplementedError("getMapEntries");
    }

    @Override
    public DataResult<Consumer<BiConsumer<Path, Path>>> getMapEntries(Path input) {
        //todo
        throw new NotImplementedError("getMapEntries");
    }

    @Override
    public DataResult<MapLike<Path>> getMap(Path input) {
        // A map of partial segment
        Map<String, Path> entries = new HashMap<>();
        for (String path : envKeys()) {
            if (path.toUpperCase(Locale.ROOT).startsWith(input.path)) {
                entries.put(path, new Path(path));
            }
        }

        return DataResult.success(new MapLike<>() {
            @Override
            public Path get(Path key) {
                return get(key.path);
            }

            @Override
            public Path get(String key) {
                String newPath = key.toUpperCase(Locale.ROOT);
                if (!input.path.isEmpty()) {
                    newPath = input.path + "_" + newPath;
                }

                for (var entryKey : entries.keySet()) {
                    if (entryKey.startsWith(newPath)) {
                        return new Path(newPath);
                    }
                }
                return null;
            }

            @Override
            public Stream<Pair<Path, Path>> entries() {
                throw new NotImplementedError("entries");
            }
        });
    }

    @Override
    public Path createMap(Stream<Pair<Path, Path>> map) {
        return illegalSerialization();
    }

    @Override
    public DataResult<Stream<Path>> getStream(Path input) {
        //todo
        throw new NotImplementedError("getStream");
    }

    @Override
    public DataResult<Consumer<Consumer<Path>>> getList(Path input) {
        // Children is every path which starts with input and is followed by a single number segment
        Map<Integer, Path> children = new HashMap<>(); // (map to ignore duplicate keys)
        for (String path : envKeys()) {
            if (!path.toUpperCase(Locale.ROOT).startsWith(input.path))
                continue;
            String[] rest = path.substring(input.path.length()).split("_");
            // First entry must be "" because we split on _00_REST
            if (rest.length < 2 || !rest[0].isEmpty())
                continue;
            // Try to parse number segment
            int index = Integer.parseInt(rest[1]);
            children.put(index, new Path(input.path + "_" + index));
        }

        // Sort on index
        Path[] sortedChildren = new Path[children.size()];
        for (var child : children.entrySet()) {
            // If the child is not within the sorted range then there was an order problem in the original (eg skipping a number)
            if (child.getKey() < 0 || child.getKey() >= children.size())
                return DataResult.error("invalid index: " + child);
            sortedChildren[child.getKey()] = child.getValue();
        }

        return DataResult.success(c -> {
            for (var child : sortedChildren) {
                Check.notNull(child, "missing list element");
                c.accept(child);
            }
        });
    }

    @Override
    public Path createList(Stream<Path> input) {
        return new Path("$$ERR"); //todo
//        return illegalSerialization();
    }

    @Override
    public Path remove(Path input, String key) {
        return illegalSerialization();
    }

    protected Collection<String> envKeys() {
        return dotenv.entries().stream().map(DotenvEntry::getKey).toList();
    }

    protected String get(Path path) {
        if (path.path().isEmpty())
            return "";
        return dotenv.get(path.path());
    }

    @Contract("-> fail")
    private Path illegalSerialization() {
        throw new IllegalStateException("serialization with EnvVarOps");
    }
}
