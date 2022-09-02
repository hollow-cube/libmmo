package unnamed.mmo.item;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.Material;
import net.minestom.server.utils.ArrayUtils;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.block.BlockUtils;
import net.minestom.server.utils.collection.ObjectArray;
import org.jetbrains.annotations.ApiStatus;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.dfu.DFUUtil;
import unnamed.mmo.dfu.ExtraCodecs;
import unnamed.mmo.util.JsonUtil;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Internal
public class ItemRegistry {

    public record Entry(
            NamespaceID namespace,
            int id,
            int stateId,
            Material material,
            Map<String, ItemComponent> components
    ) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(Entry::namespace),
                Codec.INT.fieldOf("id").forGetter(Entry::id),
                Codec.INT.fieldOf("stateId").forGetter(Entry::stateId),
                ExtraCodecs.MATERIAL.fieldOf("material").forGetter(Entry::material),
                // This is a little cursed so I will explain. Registry dispatch (see link) only works
                // on a `type` field within the object, but the ItemComponent is not guaranteed to expose
                // the type field back. We still want a Map<_type, component> so we parse both the type
                // field and component as a list of pairs. Then convert the list of pairs into a map.
                Codec.pair(Codec.STRING.fieldOf("type").codec(), ItemComponent.CODEC)
                        .listOf()
                        .xmap(DFUUtil::pairListToMap, DFUUtil::mapToPairList)
                        .optionalFieldOf("components", Map.of()).forGetter(Entry::components)
        ).apply(i, Entry::new));

    }


    // Item state -> Item
    static final ObjectArray<Item> ITEM_STATE_MAP = ObjectArray.singleThread();

    // Item id -> valid property keys (order is important for lookup)
    static final ObjectArray<ItemImpl.PropertyType[]> PROPERTIES_TYPE = ObjectArray.singleThread();

    // Item id -> Map<PropertiesValues, Item>
    static final ObjectArray<Map<ItemImpl.PropertiesHolder, ItemImpl>> POSSIBLE_STATES = ObjectArray.singleThread();

    // Namespace -> Item
    static final Registry<Item> REGISTRY = Registry.manual("items", json -> {
        final int itemId = json.get("id").getAsInt();

        // Retrieve properties
        ItemImpl.PropertyType[] propertyTypes;
        {
            JsonObject stateProperties = json.getAsJsonObject("properties");
            if (stateProperties != null) {
                final int stateCount = stateProperties.size();
                propertyTypes = new ItemImpl.PropertyType[stateCount];
                int i = 0;
                for (var entry : stateProperties.entrySet()) {
                    final var k = entry.getKey();
                    //noinspection unchecked
                    final var v = (List<String>) entry.getValue();
                    propertyTypes[i++] = new ItemImpl.PropertyType(k, v);
                }
            } else {
                propertyTypes = new ItemImpl.PropertyType[0];
            }
        }
        PROPERTIES_TYPE.set(itemId, propertyTypes);

        var entryDecoder = JsonOps.INSTANCE.withDecoder(Entry.CODEC);

        // Retrieve item states
        {
            final JsonObject stateObject = json.getAsJsonObject("states");
            final int propertiesCount = stateObject.size();
            ItemImpl[] itemValues = new ItemImpl[propertiesCount];
            ItemImpl.PropertiesHolder[] propertiesKeys = new ItemImpl.PropertiesHolder[propertiesCount];

            int propertiesOffset = 0;
            for (var stateEntry : stateObject.entrySet()) {
                final String query = stateEntry.getKey();
                final var stateOverride = stateEntry.getValue().getAsJsonObject();
                final var propertyMap = BlockUtils.parseProperties(query);
                assert propertyTypes.length == propertyMap.size();

                byte[] propertiesArray = new byte[propertyTypes.length];
                for (var entry : propertyMap.entrySet()) {
                    final byte keyIndex = findKeyIndex(propertyTypes, entry.getKey(), null);
                    final byte valueIndex = findValueIndex(propertyTypes[keyIndex], entry.getValue(), null);
                    propertiesArray[keyIndex] = valueIndex;
                }

                // Parse the entry
                var mainProperties = JsonUtil.merge(json, stateOverride);
                Entry entry = entryDecoder
                        .apply(mainProperties)
                        .getOrThrow(false, ignored -> {
                        })
                        .getFirst();

                final ItemImpl item = new ItemImpl(entry, propertiesArray, 1);
                ITEM_STATE_MAP.set(item.stateId(), item);
                propertiesKeys[propertiesOffset] = new ItemImpl.PropertiesHolder(propertiesArray);
                itemValues[propertiesOffset++] = item;
            }

            POSSIBLE_STATES.set(itemId, ArrayUtils.toMap(propertiesKeys, itemValues, propertiesOffset));
        }

        // Default state
        final int defaultStateId = json.get("defaultStateId").getAsInt();
        return Item.fromStateId(defaultStateId);
    });

    // Item id -> Item
    static final ObjectArray<Item> ID_TO_ITEM = REGISTRY.unsafeIntegerIndex(Item::id);


    private static byte findKeyIndex(ItemImpl.PropertyType[] properties, String key, ItemImpl item) {
        for (byte i = 0; i < properties.length; i++) {
            if (properties[i].key().equals(key)) return i;
        }
        if (item != null) {
            throw new IllegalArgumentException("Property " + key + " is not valid for item " + item);
        } else {
            throw new IllegalArgumentException("Unknown property key: " + key);
        }
    }

    private static byte findValueIndex(ItemImpl.PropertyType propertyType, String value, ItemImpl item) {
        final List<String> values = propertyType.values();
        final byte index = (byte) values.indexOf(value);
        if (index != -1) return index;
        if (item != null) {
            throw new IllegalArgumentException("Property " + propertyType.key() + " value " + value + " is not valid for item " + item);
        } else {
            throw new IllegalArgumentException("Unknown property value: " + value);
        }
    }

    static {
        PROPERTIES_TYPE.trim();
        POSSIBLE_STATES.trim();
        ITEM_STATE_MAP.trim();
    }

}
