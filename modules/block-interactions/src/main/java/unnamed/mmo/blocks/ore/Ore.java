package unnamed.mmo.blocks.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.ExtraCodecs;

public record Ore(
        @NotNull NamespaceID namespace,
        int health
) implements Resource {

    // Impl

    static final Tag<Ore> TAG = Tag.String("ore_id").map(Ore::fromNamespaceId, Ore::name);

    public @NotNull Block asBlock() {
        //todo do not hardcode gold ore
        return Block.GOLD_ORE.withTag(TAG, this);
    }

    public static @Nullable Ore fromBlock(@NotNull Block block) {
        return block.getTag(TAG);
    }


    // Registry

    public static final Codec<Ore> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(Ore::namespace),
            Codec.INT.fieldOf("health").forGetter(Ore::health)
    ).apply(i, Ore::new));

    public static final Registry<Ore> REGISTRY = Registry.codec("ore", CODEC);

    public static @UnknownNullability Ore fromNamespaceId(@NotNull NamespaceID namespace) {
        return REGISTRY.get(namespace);
    }

    public static @UnknownNullability Ore fromNamespaceId(@NotNull String namespace) {
        return REGISTRY.get(namespace);
    }

}
