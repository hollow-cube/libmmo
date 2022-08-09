package unnamed.mmo.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.DFUUtil;
import unnamed.mmo.util.ExtraCodecs;

/**
 * See <a href="https://minecraft.fandom.com/wiki/Loot_table#Number_Providers">Number Providers</a> for more information.
 */
public interface NumberProvider {

    // Factory

    static @NotNull NumberProvider constant(@NotNull Number value) {
        return new ConstantNumberProvider(value);
    }


    // Impl

    Codec<NumberProvider> CODEC = Codec.either(
            Factory.CODEC.<NumberProvider>dispatch(Factory::from, Factory::codec),
            // Handle the case of providing a single value value. If serialized back it will
            // come out as a full {"type": "constant", "value": ...}
            ExtraCodecs.NUMBER.xmap(ConstantNumberProvider::new, ConstantNumberProvider::value)
    ).xmap(DFUUtil::value, Either::left);

    long nextLong(@NotNull NumberSource numbers);

    double nextDouble(@NotNull NumberSource numbers);


    abstract class Factory<P extends NumberProvider> implements Resource {
        public static final Codec<Factory<?>> CODEC = Codec.STRING.xmap(ns -> NumberProviderRegistry.FACTORIES.get(ns), Factory::name);

        private final NamespaceID namespace;
        private final Class<P> type;
        private final Codec<P> codec;

        public Factory(NamespaceID namespace, Class<P> type, Codec<P> codec) {
            this.namespace = namespace;
            this.type = type;
            this.codec = codec;
        }

        @Override
        public @NotNull NamespaceID namespace() {
            return this.namespace;
        }

        public @NotNull Class<?> type() {
            return this.type;
        }

        public @NotNull Codec<P> codec() {
            return this.codec;
        }

        // Static helpers
        static <P extends NumberProvider> @NotNull Factory<P> from(@NotNull NumberProvider provider) {
            //noinspection unchecked
            return (Factory<P>) NumberProviderRegistry.FACTORIES_BY_TYPE.get(provider.getClass());
        }
    }


    // Static helpers


}
