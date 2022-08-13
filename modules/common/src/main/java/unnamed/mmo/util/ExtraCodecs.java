package unnamed.mmo.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

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

    public static final Codec<Material> MATERIAL = Codec.STRING.xmap(Material::fromNamespaceId, Material::name);

    public static final Codec<Block> BLOCK = Codec.STRING.xmap(Block::fromNamespaceId, Block::name);

    public static @NotNull MapCodec<String> string(@NotNull String name, @Nullable String defaultValue) {
        return Codec.STRING.optionalFieldOf(name, defaultValue);
    }

    public static <T> @NotNull Codec<T> lazy(Supplier<Codec<T>> init) {
        return new LazyCodec<>(init);
    }


    public static class LazyCodec<T> implements Codec<T> {
        private final Supplier<Codec<T>> init;
        private Codec<T> value = null;

        LazyCodec(Supplier<Codec<T>> init) {
            this.init = init;
        }

        @Override
        public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
            if (value == null) value = init.get();
            return value.decode(ops, input);
        }

        @Override
        public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
            if (value == null) value = init.get();
            return value.encode(input, ops, prefix);
        }
    }


}
