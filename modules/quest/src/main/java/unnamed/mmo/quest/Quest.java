package unnamed.mmo.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.quest.objective.Objective;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.ExtraCodecs;

public record Quest(
        @NotNull NamespaceID namespace,
        //todo
//        @NotNull List<Object> requirements,
        @NotNull Objective objective
) implements Resource {

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(Quest::namespace),
//            Codec.unit(null).listOf().optionalFieldOf("requirements", List.of()).forGetter(Quest::requirements),
            Objective.CODEC.fieldOf("objective").forGetter(Quest::objective)
    ).apply(i, Quest::new));

    public static final Registry<Quest> REGISTRY = Registry.codec("quest", CODEC);

    public static @Nullable Quest fromNamespaceId(@NotNull String namespace) {
        return REGISTRY.get(namespace);
    }

}
