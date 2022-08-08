package unnamed.mmo.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public record ItemImpl(
        @NotNull ItemRegistry.Entry registry,
        byte @NotNull [] propertiesArray,
        int amount
) implements Item {

    @Contract(pure = true)
    public @NotNull NamespaceID namespace() {
        return registry().namespace();
    }

    @Contract(pure = true)
    public int id() {
        return registry().id();
    }

    @Contract(pure = true)
    public int stateId() {
        return registry().stateId();
    }

    @Override
    public @Unmodifiable @NotNull Map<String, String> properties() {
        final PropertyType[] propertyTypes = ItemRegistry.PROPERTIES_TYPE.get(id());
        assert propertyTypes != null;
        final int length = propertyTypes.length;
        String[] keys = new String[length];
        String[] values = new String[length];
        for (int i = 0; i < length; i++) {
            var property = propertyTypes[i];
            keys[i] = property.key();
            values[i] = property.values().get(propertiesArray[i]);
        }
        return Map.class.cast(Object2ObjectMaps.unmodifiable(new Object2ObjectArrayMap<>(keys, values, length)));
    }

    @Contract(pure = true)
    public @NotNull Material material() {
        return registry().material();
    }

    @Override
    public <C extends Component> @Nullable C getComponent(@NotNull String namespace) {
        //noinspection unchecked
        return (C) registry().components().get(namespace);
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public Item withAmount(int amount) {
        return new ItemImpl(registry, propertiesArray, amount);
    }


    // Equality impl

    @Override
    public String toString() {
        return String.format("%s{properties=%s,amount=%d}", name(), properties(), amount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemImpl item)) return false;
        return stateId() == item.stateId() && amount() == item.amount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateId());
    }


    // Properties

    record PropertyType(String key, List<String> values) {
    }

    static final class PropertiesHolder {
        private final byte[] properties;
        private final int hashCode;

        public PropertiesHolder(byte[] properties) {
            this.properties = properties;
            this.hashCode = Arrays.hashCode(properties);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemImpl.PropertiesHolder that)) return false;
            return Arrays.equals(properties, that.properties);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
