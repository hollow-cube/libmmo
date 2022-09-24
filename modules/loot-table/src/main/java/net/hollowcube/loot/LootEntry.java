package net.hollowcube.loot;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.ResourceFactory;

import java.util.List;

public interface LootEntry<T> {

    Codec<LootEntry<?>> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);

    @NotNull List<@NotNull Option<T>> generate(@NotNull LootContext context);

    record Option<T>(
            @NotNull List<T> loot,
            int weight
    ) {}


    abstract class Factory extends ResourceFactory<LootEntry<?>> {
        static Registry<Factory> REGISTRY = Registry.service("loot_entries", Factory.class);
        static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        static Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.required(ns), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends LootEntry<?>> type, Codec<? extends LootEntry<?>> codec) {
            super(namespace, type, codec);
        }

        public static @NotNull Factory from(@NotNull LootEntry<?> entry) {
            return TYPE_REGISTRY.get(entry.getClass());
        }
    }

}
