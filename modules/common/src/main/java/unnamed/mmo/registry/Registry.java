package unnamed.mmo.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.collection.ObjectArray;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unnamed.mmo.Env;
import unnamed.mmo.dfu.DFUUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A (publically) immutable list of {@link Resource}s, indexed by {@link NamespaceID}.
 * <p>
 * Registries can be created in a variety of ways with varying degrees of comlepexity. Ores, item components
 * and item entries serve as examples with varying complexity and should be used as an example.
 * <p>
 * The most simple option is {@link #codec(String, Codec)}. This will create a registry from a single data file
 * with the name {@code ${name}.json} which must contain an array of objects, each conforming to the given
 * {@link Codec}. Ores are an example using this pattern.
 * <p>
 * Another very common pattern is used in NumberProviders. This case can be used when a field may contain a variety
 * of implementations, identified by their key. First, an interface should be created for the resource, as well as
 * a Factory inner abstract class which implements ResourceFactory. A {@link #service(String, java.lang.Class)}
 * registry can be created to load all implementations of the factory. From there, registry dispatch may be used on
 * the codec to create a codec for all implementations of the interface. Finally, each implementation must implement
 * a Factory class and register it with {@link com.google.auto.service.AutoService}. See NumberProvider for more detail.
 * <p>
 * Finally, if some complexity is needed during the loading process of static resources, {@link #manual(String, Function)}
 * can be used to individually process each resource. The loading mechanism is the same as {@link #codec(String, Codec)}.
 * Item registry entries (ItemRegistry.Entry) are an example using this pattern.
 *
 * @param <T> The type of resource stored in the registry.
 */
public interface Registry<T extends Resource> {
    //todo registry should implement Codec i think

    // Factory

    static <T extends Resource> Registry<T> manual(@NotNull String name, Supplier<Collection<T>> supplier) {
        Map<String, T> registry = new HashMap<>();
        for (T element : supplier.get()) {
            registry.put(element.name(), element);
        }

        // Registries may not be empty in strict mode
        Env.strictValidation(
                "Empty registry for manual resource: " + name,
                registry::isEmpty
        );

        return new MapRegistry<>(registry);
    }

    static <T extends Resource> Registry<T> service(@NotNull String name, Class<T> type) {
        Map<String, T> registry = new HashMap<>();
        for (T elem : ServiceLoader.load(type)) {
            registry.put(elem.name(), elem);
        }

        // Registries may not be empty in strict mode
        Env.strictValidation(
                "Empty registry for manual resource: " + name,
                registry::isEmpty
        );

        return new MapRegistry<>(registry);
    }

    static <T extends Resource> Registry<T> manual(@NotNull String name, @NotNull Function<JsonObject, T> mapper) {
        Map<String, T> registry = new HashMap<>();

        JsonArray content = Resource.loadJsonArray(name + ".json");
        for (JsonElement elem : content) {
            Check.stateCondition(!elem.isJsonObject(), "Registry items must be json objects");

            try {
                T element = mapper.apply(elem.getAsJsonObject());
                registry.put(element.name(), element);
            } catch (Throwable e) {
                Logger logger = LoggerFactory.getLogger(Registry.class);
                logger.error("Failed to load registry item in {}: {}", name, elem, e);

                Env.strictValidation("Registry item failure", () -> true);
            }
        }

        // Registries may not be empty in strict mode
        Env.strictValidation(
                "Empty registry for resource: " + name,
                registry::isEmpty
        );

        return new MapRegistry<>(registry);
    }

    /**
     * Create a registry from a codec directly. The registry item must have a "namespace" field for the ID.
     *
     * @param codec The codec to deserialize with
     * @param name  The name of the data file to load from (.json is appended)
     */
    static <T extends Resource> Registry<T> codec(@NotNull String name, @NotNull Codec<T> codec) {
        JsonArray content = Resource.loadJsonArray(name + ".json");

        // Create a modified encoder that converts to a map. See the description of ItemRegistry.Entry.CODEC
        // for more notes on the technique used here.
        Codec<Map<String, T>> mapCodec = Codec.pair(Codec.STRING.fieldOf("namespace").codec(), codec)
                .listOf()
                .xmap(DFUUtil::pairListToMap, DFUUtil::mapToPairList);

        Map<String, T> registry = null; // null is just to make javac happy, it can never happen.
        try {
            registry = JsonOps.INSTANCE
                    .withDecoder(mapCodec)
                    .apply(content)
                    .getOrThrow(false, ignored -> {})
                    .getFirst();
        } catch (RuntimeException e) {
            Logger logger = LoggerFactory.getLogger(Registry.class);
            logger.error("Failed to create registry {}", name, e);

            // This is a fatal error. We should never allow a server to start up with a broken registry.
            System.exit(1);
        }

        // Registries may not be empty in strict mode
        Env.strictValidation(
                "Empty registry for resource: " + name,
                registry::isEmpty
        );

        return new MapRegistry<>(registry);
    }


    // Impl

    @Nullable T getRaw(String namespace);

    default @UnknownNullability T get(String namespace) {
        if (namespace == null) return null;
        return getRaw(namespace.contains(":") ? namespace : "minecraft:" + namespace);
    }

    default @UnknownNullability T get(NamespaceID namespace) {
        if (namespace == null) return null;
        return getRaw(namespace.asString());
    }

    default @NotNull T required(String namespace) {
        T result = get(namespace);
        Check.notNull(result, "Missing required registry entry: " + namespace);
        return result;
    }


    @NotNull Collection<T> values();

    int size();


    // Derivatives

    interface Index<K, T extends Resource> {

        @UnknownNullability T get(K key);

    }

    @NotNull <K> Index<K, T> index(Function<T, K> mapper);


    // Below here is kinda cursed, will fix eventually but works for now.

    default @NotNull ObjectArray<T> unsafeIntegerIndex(Function<T, Integer> getter) {
        Collection<T> values = values();

        ObjectArray<T> index = ObjectArray.singleThread(values.size());
        for (T elem : values)
            index.set(getter.apply(elem), elem);

        index.trim();
        return index;
    }
}
