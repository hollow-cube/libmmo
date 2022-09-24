package net.hollowcube.registry;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public interface TestResource extends Resource {

    Codec<TestResource> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);

    abstract class Factory extends ResourceFactory<TestResource> {
        static Registry<Factory> REGISTRY = Registry.service("test_resource", Factory.class);
        static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        static Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.required(ns), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends TestResource> type, Codec<? extends TestResource> codec) {
            super(namespace, type, codec);
        }

        public static @NotNull Factory from(@NotNull TestResource resource) {
            return TYPE_REGISTRY.get(resource.getClass());
        }
    }

}
