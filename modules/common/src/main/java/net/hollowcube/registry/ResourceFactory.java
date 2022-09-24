package net.hollowcube.registry;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public abstract class ResourceFactory<T> implements Resource {
    private final NamespaceID namespace;
    private final Class<? extends T> type;
    private final Codec<? extends T> codec;

    public ResourceFactory(NamespaceID namespace, Class<? extends T> type, Codec<? extends T> codec) {
        this.namespace = namespace;
        this.type = type;
        this.codec = codec;
    }

    public ResourceFactory(String namespace, Class<? extends T> type, Codec<? extends T> codec) {
        this.namespace = NamespaceID.from(namespace);
        this.type = type;
        this.codec = codec;
    }

    @Override
    public @NotNull NamespaceID namespace() {
        return this.namespace;
    }

    public @NotNull Class<? extends T> type() {
        return this.type;
    }

    public @NotNull Codec<? extends T> codec() {
        return this.codec;
    }
}
