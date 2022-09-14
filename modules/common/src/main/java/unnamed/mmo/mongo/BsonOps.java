package unnamed.mmo.mongo;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import org.bson.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * DFU {@link DynamicOps} implementation for BSON values.
 * <p>
 * The official BSON library _does_ support direct reflection based object mapping, however,
 * since codecs are being used in many other places, it makes some sense to use them here too.
 * It generalizes storage implementations to simply use the codec for a type.
 * <p>
 * todo its possible this will be problematic because it is not using the real bson types (eg bson date),
 *      but I dont expect this to be a huge issue.
 */
public class BsonOps implements DynamicOps<BsonValue> {

    public static final BsonOps INSTANCE = new BsonOps();

    private BsonOps() {}

    @Override
    public BsonValue empty() {
        return BsonNull.VALUE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, BsonValue input) {
        if (input instanceof BsonDocument) {
            return convertMap(outOps, input);
        }
        if (input instanceof BsonArray) {
            return convertList(outOps, input);
        }
        if (input instanceof BsonNull) {
            return outOps.empty();
        }
        //todo other bson types
        return null;
    }

    @Override
    public DataResult<Number> getNumberValue(BsonValue input) {
        if (input instanceof BsonNumber number) {
            return DataResult.success(new BsonNumberWrapper(number));
        }
        if (input instanceof BsonBoolean bool) {
            return DataResult.success(bool.getValue() ? 1 : 0);
        }
        return DataResult.error("Not a number: " + input);
    }

    @Override
    public BsonValue createNumeric(Number i) {
        if (i instanceof Byte || i instanceof Short || i instanceof Integer) {
            return new BsonInt32(i.intValue());
        }
        if (i instanceof Long) {
            return new BsonInt64(i.longValue());
        }
        if (i instanceof Float || i instanceof Double) {
            return new BsonDouble(i.doubleValue());
        }
        throw new IllegalArgumentException("Unknown number type: " + i.getClass().getSimpleName() + " (" + i + ")");
    }

    @Override
    public DataResult<String> getStringValue(BsonValue input) {
        if (input instanceof BsonString string) {
            return DataResult.success(string.getValue());
        }
        return DataResult.error("Not a string: " + input);
    }

    @Override
    public BsonValue createString(String value) {
        return new BsonString(value);
    }

    @Override
    public DataResult<BsonValue> mergeToList(BsonValue list, BsonValue value) {
        if (!(list instanceof BsonArray) && list != empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final BsonArray result = new BsonArray();
        if (list != empty()) {
            result.addAll(list.asArray());
        }
        result.add(value);
        return DataResult.success(result);
    }

    @Override
    public DataResult<BsonValue> mergeToList(BsonValue list, List<BsonValue> values) {
        if (!(list instanceof BsonArray) && list != empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final BsonArray result = new BsonArray();
        if (list != empty()) {
            result.addAll(list.asArray());
        }
        result.addAll(values);
        return DataResult.success(result);
    }

    @Override
    public DataResult<BsonValue> mergeToMap(BsonValue map, BsonValue key, BsonValue value) {
        if (!(map instanceof BsonDocument) && map != empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        if (!(key instanceof BsonString keyString)) {
            return DataResult.error("key is not a string: " + key, map);
        }

        final BsonDocument output = new BsonDocument();
        if (map != empty()) {
            output.putAll(map.asDocument());
        }
        output.put(keyString.getValue(), value);

        return DataResult.success(output);
    }

    @Override
    public DataResult<BsonValue> mergeToMap(BsonValue map, MapLike<BsonValue> values) {
        if (!(map instanceof BsonDocument) && map != empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }

        final BsonDocument output = new BsonDocument();
        if (map != empty()) {
            output.putAll(map.asDocument());
        }

        final List<BsonValue> missed = new ArrayList<>();

        values.entries().forEach(entry -> {
            final BsonValue key = entry.getFirst();
            if (!(key instanceof BsonString keyString)) {
                missed.add(key);
                return;
            }
            output.put(keyString.getValue(), entry.getSecond());
        });

        if (!missed.isEmpty()) {
            return DataResult.error("some keys are not strings: " + missed, output);
        }

        return DataResult.success(output);
    }

    @Override
    public DataResult<Stream<Pair<BsonValue, BsonValue>>> getMapValues(BsonValue input) {
        if (!(input instanceof BsonDocument document)) {
            return DataResult.error("Not a bson document: " + input);
        }
        return DataResult.success(document.entrySet().stream().map(entry -> Pair.of(
                new BsonString(entry.getKey()),
                entry.getValue() instanceof BsonNull ? null : entry.getValue())
        ));
    }

    @Override
    public DataResult<Consumer<BiConsumer<BsonValue, BsonValue>>> getMapEntries(BsonValue input) {
        if (!(input instanceof BsonDocument document)) {
            return DataResult.error("Not a bson document: " + input);
        }
        return DataResult.success(c -> {
            for (final Map.Entry<String, BsonValue> entry : document.entrySet()) {
                c.accept(createString(entry.getKey()), entry.getValue() instanceof BsonNull ? null : entry.getValue());
            }
        });
    }

    @Override
    public DataResult<MapLike<BsonValue>> getMap(BsonValue input) {
        if (!(input instanceof BsonDocument document)) {
            return DataResult.error("Not a bson document: " + input);
        }
        return DataResult.success(new MapLike<>() {
            @Override
            public @Nullable BsonValue get(final BsonValue key) {
                final BsonValue value = document.get(key.asString().getValue());
                return value instanceof BsonNull ? null : value;
            }

            @Override
            public @Nullable BsonValue get(final String key) {
                final BsonValue value = document.get(key);
                return value instanceof BsonNull ? null : value;
            }

            @Override
            public Stream<Pair<BsonValue, BsonValue>> entries() {
                return document.entrySet().stream().map(e -> Pair.of(new BsonString(e.getKey()), e.getValue()));
            }

            @Override
            public String toString() {
                return "MapLike[" + document + "]";
            }
        });
    }

    @Override
    public BsonValue createMap(Stream<Pair<BsonValue, BsonValue>> map) {
        final BsonDocument result = new BsonDocument();
        map.forEach(p -> result.put(p.getFirst().asString().getValue(), p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<BsonValue>> getStream(BsonValue input) {
        if (input instanceof BsonArray array) {
            return DataResult.success(array.stream().map(e -> e instanceof BsonNull ? null : e));
        }
        return DataResult.error("Not a bson array: " + input);
    }

    @Override
    public DataResult<Consumer<Consumer<BsonValue>>> getList(BsonValue input) {
        if (input instanceof BsonArray array) {
            return DataResult.success(c -> {
                for (final BsonValue value : array) {
                    c.accept(value);
                }
            });
        }
        return DataResult.error("Not a bson array: " + input);
    }

    @Override
    public BsonValue createList(Stream<BsonValue> input) {
        final BsonArray result = new BsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public BsonValue remove(BsonValue input, String key) {
        if (input instanceof BsonDocument document) {
            final BsonDocument result = new BsonDocument();
            document.entrySet().stream()
                    .filter(entry -> !Objects.equals(entry.getKey(), key))
                    .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }



    private static final class BsonNumberWrapper extends Number {
        private final BsonNumber delegate;

        public BsonNumberWrapper(BsonNumber delegate) {
            this.delegate = delegate;
        }

        @Override
        public int intValue() {
            return delegate.intValue();
        }

        @Override
        public long longValue() {
            return delegate.longValue();
        }

        @Override
        public float floatValue() {
            return (float) delegate.doubleValue();
        }

        @Override
        public double doubleValue() {
            return delegate.doubleValue();
        }
    }
}
