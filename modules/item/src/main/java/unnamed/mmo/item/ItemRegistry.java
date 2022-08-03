package unnamed.mmo.item;

import net.minestom.server.item.Material;
import net.minestom.server.utils.ArrayUtils;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.block.BlockUtils;
import net.minestom.server.utils.collection.MergedMap;
import net.minestom.server.utils.collection.ObjectArray;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.component.ComponentHandler;
import unnamed.mmo.item.component.ItemComponent;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minestom.server.registry.Registry.Properties;

public class ItemRegistry {

    public static final class Entry {
        private final NamespaceID namespace;
        private final int id;
        private final int stateId;
        private final Material material;

        private final Map<String, ItemComponent> components;

        private Entry(String namespace, Properties props) {
            this.namespace = NamespaceID.from(namespace);
            this.id = props.getInt("id");
            this.stateId = props.getInt("stateId");

            this.material = Material.fromNamespaceId(props.getString("material"));

            var componentMap = props.section("components");
            var components = new HashMap<String, ItemComponent>();
            for (var entry : componentMap) {
                String componentNamespace = entry.getKey();
                ComponentHandler<?> handler = ComponentHandler.fromNamespaceId(componentNamespace);
                Check.notNull(handler, "Missing item component handler '" + componentNamespace + "'");

                ItemComponent component = handler.factory().apply(Properties.fromMap((Map<String, Object>) entry.getValue()));
                components.put(componentNamespace, component);
            }
            this.components = Map.copyOf(components);
        }

        public @NotNull NamespaceID namespace() {
            return namespace;
        }

        public int id() {
            return id;
        }

        public int stateId() {
            return stateId;
        }

        public Material material() {
            return material;
        }

        public Map<String, ItemComponent> components() {
            return components;
        }
    }

    private static final Resource.Type RESOURCE = new Resource.Type("item");

    // Item state -> item object
    static final ObjectArray<Item> ITEM_STATE_MAP = ObjectArray.singleThread();

    // Item id -> valid property keys (order is important for lookup)
    static final ObjectArray<ItemImpl.PropertyType[]> PROPERTIES_TYPE = ObjectArray.singleThread();

    // Item id -> Map<PropertiesValues, Item>
    static final ObjectArray<Map<ItemImpl.PropertiesHolder, ItemImpl>> POSSIBLE_STATES = ObjectArray.singleThread();

    static Registry.Container.Loader<Item> LOADER = (namespace, props) -> {
        final int itemId = props.getInt("id");

        // Retrieve properties
        ItemImpl.PropertyType[] propertyTypes;
        {
            Properties stateProperties = props.section("properties");
            if (stateProperties != null) {
                final int stateCount = stateProperties.size();
                propertyTypes = new ItemImpl.PropertyType[stateCount];
                int i = 0;
                for (var entry : stateProperties) {
                    final var k = entry.getKey();
                    final var v = (List<String>) entry.getValue();
                    propertyTypes[i++] = new ItemImpl.PropertyType(k, v);
                }
            } else {
                propertyTypes = new ItemImpl.PropertyType[0];
            }
        }
        PROPERTIES_TYPE.set(itemId, propertyTypes);

        // Retrieve item states
        {
            final Properties stateObject = props.section("states");
            final int propertiesCount = stateObject.size();
            ItemImpl[] itemValues = new ItemImpl[propertiesCount];
            ItemImpl.PropertiesHolder[] propertiesKeys = new ItemImpl.PropertiesHolder[propertiesCount];

            int propertiesOffset = 0;
            for (var stateEntry : stateObject) {
                final String query = stateEntry.getKey();
                final var stateOverride = (Map<String, Object>) stateEntry.getValue();
                final var propertyMap = BlockUtils.parseProperties(query);
                assert propertyTypes.length == propertyMap.size();

                byte[] propertiesArray = new byte[propertyTypes.length];
                for (var entry : propertyMap.entrySet()) {
                    final byte keyIndex = findKeyIndex(propertyTypes, entry.getKey(), null);
                    final byte valueIndex = findValueIndex(propertyTypes[keyIndex], entry.getValue(), null);
                    propertiesArray[keyIndex] = valueIndex;
                }

                var mainProperties = net.minestom.server.registry.Registry.Properties.fromMap(new MergedMap<>(stateOverride, props.asMap()));
                final ItemImpl block = new ItemImpl(new Entry(namespace, mainProperties), propertiesArray, 1);
                ITEM_STATE_MAP.set(block.stateId(), block);
                propertiesKeys[propertiesOffset] = new ItemImpl.PropertiesHolder(propertiesArray);
                itemValues[propertiesOffset++] = block;
            }

            POSSIBLE_STATES.set(itemId, ArrayUtils.toMap(propertiesKeys, itemValues, propertiesOffset));
        }

        // Default state
        final int defaultStateId = props.getInt("defaultStateId");
        return ItemImpl.getState(defaultStateId);
    };

    static final Registry.IdContainer<Item> CONTAINER = Registry.createIdContainer(RESOURCE, LOADER);

    static {
        PROPERTIES_TYPE.trim();
        POSSIBLE_STATES.trim();
        ITEM_STATE_MAP.trim();
    }


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

}
