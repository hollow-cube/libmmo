package net.hollowcube.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.dfu.ExtraCodecs;
import net.hollowcube.entity.task.Task;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.Resource;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public record EntityType(
        @NotNull NamespaceID namespace,
        @NotNull NamespaceID model,
        @NotNull Task.Spec behavior
) implements Resource {

    public static final Codec<EntityType> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(EntityType::namespace),
            ExtraCodecs.NAMESPACE_ID.fieldOf("model").forGetter(EntityType::model),
            ExtraCodecs.NAMESPACE_ID.xmap(ns -> Task.Spec.REGISTRY.required(ns.asString()), Task.Spec::namespace)
                    .fieldOf("behavior").forGetter(EntityType::behavior)
    ).apply(i, EntityType::new));

    public static final Registry<EntityType> REGISTRY = Registry.codec("entities", CODEC);

}
