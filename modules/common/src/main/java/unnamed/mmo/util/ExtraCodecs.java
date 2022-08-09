package unnamed.mmo.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExtraCodecs {
    private ExtraCodecs() {}

    public static final PrimitiveCodec<Number> NUMBER = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Number> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Number value) {
            return ops.createNumeric(value);
        }
    };

    public static final Codec<NamespaceID> NAMESPACE_ID = Codec.STRING.xmap(NamespaceID::from, NamespaceID::asString);

    public static final Codec<Material> MATERIAL = Codec.STRING.xmap(Material::fromNamespaceId, Material::toString);

    public static @NotNull MapCodec<String> string(@NotNull String name, @Nullable String defaultValue) {
        return Codec.STRING.optionalFieldOf(name, defaultValue);
    }


}
