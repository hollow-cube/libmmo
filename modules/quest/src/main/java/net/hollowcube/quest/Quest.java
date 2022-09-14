package net.hollowcube.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.quest.objective.Objective;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.dfu.ExtraCodecs;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.Resource;

public record Quest(
        @NotNull NamespaceID namespace,
        @NotNull Objective objective
) implements Resource {

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(Quest::namespace),
            Objective.CODEC.fieldOf("objective").forGetter(Quest::objective)
    ).apply(i, Quest::new));

    public static final Registry<Quest> REGISTRY = Registry.codec("quest", CODEC);

    public static @Nullable Quest fromNamespaceId(@NotNull String namespace) {
        return REGISTRY.get(namespace);
    }

}
