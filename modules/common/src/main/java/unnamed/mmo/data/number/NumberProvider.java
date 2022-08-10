package unnamed.mmo.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.ResourceFactory;
import unnamed.mmo.util.DFUUtil;
import unnamed.mmo.util.ExtraCodecs;

/**
 * See <a href="https://minecraft.fandom.com/wiki/Loot_table#Number_Providers">Number Providers</a> for more information.
 */
public interface NumberProvider {

    static @NotNull NumberProvider constant(@NotNull Number value) {
        return new ConstantNumberProvider(value);
    }

    Codec<NumberProvider> CODEC = Codec.either(
            Factory.CODEC.<NumberProvider>dispatch(Factory::from, Factory::codec),
            // Handle the case of providing a single value value. If serialized back it will
            // come out as a full {"type": "constant", "value": ...}
            ExtraCodecs.NUMBER.xmap(ConstantNumberProvider::new, ConstantNumberProvider::value)
    ).xmap(DFUUtil::value, Either::left);


    // Impl

    long nextLong(@NotNull NumberSource numbers);

    double nextDouble(@NotNull NumberSource numbers);


    abstract class Factory extends ResourceFactory<NumberProvider> {
        static Registry<Factory> REGISTRY = Registry.service("number_providers", NumberProvider.Factory.class);
        static Registry.Index<Class<?>, NumberProvider.Factory> TYPE_REGISTRY = REGISTRY.index(NumberProvider.Factory::type);

        public static final Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.get(ns), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends NumberProvider> type, Codec<? extends NumberProvider> codec) {
            super(namespace, type, codec);
        }

        // Static helpers

        static @NotNull Factory from(@NotNull NumberProvider provider) {
            return TYPE_REGISTRY.get(provider.getClass());
        }
    }

}
