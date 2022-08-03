package unnamed.mmo.registry;

import com.google.gson.ToNumberPolicy;
import com.google.gson.stream.JsonReader;
import net.minestom.server.utils.collection.ObjectArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static net.minestom.server.registry.Registry.Properties;

public class Registry {
    @TestOnly
    static Path DATA_PATH = Path.of(System.getProperty("unnamed.data.dir", "data"));

    @ApiStatus.Internal
    public static <T extends Resource> Container<T> createContainer(Resource.Type resource, Container.Loader<T> loader) {
        var entries = load(resource);
        Map<String, T> namespaces = new HashMap<>(entries.size());
        for (var entry : entries.entrySet()) {
            final String namespace = entry.getKey();
            final Properties properties = Properties.fromMap(entry.getValue());
            final T value = loader.get(namespace, properties);
            namespaces.put(value.name(), value);
        }
        return new Container<>(resource, namespaces);
    }

    @ApiStatus.Internal
    public static <T extends Resource.Id> IdContainer<T> createIdContainer(Resource.Type resource, Container.Loader<T> loader) {
        var entries = load(resource);
        Map<String, T> namespaces = new HashMap<>(entries.size());
        ObjectArray<T> ids = ObjectArray.singleThread(entries.size());
        for (var entry : entries.entrySet()) {
            final String namespace = entry.getKey();
            final Properties properties = Properties.fromMap(entry.getValue());
            final T value = loader.get(namespace, properties);
            ids.set(value.id(), value);
            namespaces.put(value.name(), value);
        }
        return new IdContainer<>(resource, namespaces, ids);
    }

    private static Map<String, Map<String, Object>> load(Resource.Type resource) {
        Map<String, Map<String, Object>> map = new HashMap<>();

        Path file = DATA_PATH.resolve(resource.name().toLowerCase() + ".json");
        try (BufferedReader resourceStream = Files.newBufferedReader(file)) {
            try (JsonReader reader = new JsonReader(resourceStream)) {
                reader.beginObject();
                while (reader.hasNext()) map.put(reader.nextName(), (Map<String, Object>) readObject(reader));
                reader.endObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static class Container<T extends Resource> {
        private final Resource.Type resource;
        private final Map<String, T> namespaces;

        public Container(Resource.Type resource, Map<String, T> namespaces) {
            this.resource = resource;
            this.namespaces = Map.copyOf(namespaces);
        }

        public T get(@NotNull String namespace) {
            return namespaces.get(namespace);
        }

        public T getSafe(@NotNull String namespace) {
            return get(namespace.contains(":") ? namespace : "minecraft:" + namespace);
        }

        public Collection<T> values() {
            return namespaces.values();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Container<?> container)) return false;
            return resource == container.resource;
        }

        @Override
        public int hashCode() {
            return Objects.hash(resource);
        }

        @FunctionalInterface
        public interface Loader<T extends Resource> {
            T get(String namespace, net.minestom.server.registry.Registry.Properties properties);
        }
    }

    public static class IdContainer<T extends Resource.Id> extends Container<T> {
        private final ObjectArray<T> ids;

        public IdContainer(Resource.Type resource, Map<String, T> namespaces, ObjectArray<T> ids) {
            super(resource, namespaces);
            this.ids = ids;
            ids.trim();
        }

        public T getId(int id) {
            return ids.get(id);
        }

    }

    public static Object readObject(JsonReader reader) throws IOException {
        return switch (reader.peek()) {
            case BEGIN_ARRAY -> {
                List<Object> list = new ArrayList<>();
                reader.beginArray();
                while (reader.hasNext()) list.add(readObject(reader));
                reader.endArray();
                yield list;
            }
            case BEGIN_OBJECT -> {
                Map<String, Object> map = new HashMap<>();
                reader.beginObject();
                while (reader.hasNext()) map.put(reader.nextName(), readObject(reader));
                reader.endObject();
                yield map;
            }
            case STRING -> reader.nextString();
            case NUMBER -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader);
            case BOOLEAN -> reader.nextBoolean();
            default -> throw new IllegalStateException("Invalid peek: " + reader.peek());
        };
    }

}
