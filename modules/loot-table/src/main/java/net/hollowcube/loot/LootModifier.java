package net.hollowcube.loot;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.ResourceFactory;

public interface LootModifier {

    Codec<LootModifier> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);

    //todo there is a contract here that apply MUST return the same type.
    // Perhaps should validate this, or introduce some generic to help perhaps
    // Will do in the future
    @NotNull Object apply(@NotNull Object input);


    abstract class Factory extends ResourceFactory<LootModifier> {
        static Registry<Factory> REGISTRY = Registry.service("loot_modifiers", LootModifier.Factory.class);
        static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        static Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.get(ns), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends LootModifier> type, Codec<? extends LootModifier> codec) {
            super(namespace, type, codec);
        }

        public static @NotNull Factory from(@NotNull LootModifier modifier) {
            return TYPE_REGISTRY.get(modifier.getClass());
        }
    }

}
