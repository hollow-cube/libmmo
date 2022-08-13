package unnamed.mmo.blocks.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unnamed.mmo.blocks.ore.handler.OreBlockHandler;
import unnamed.mmo.loot.LootTable;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.ExtraCodecs;

public record Ore(
        @NotNull NamespaceID namespace,
        @NotNull Block oreBlock,
        int health,
        @NotNull NamespaceID lootTableId
) implements Resource {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ore.class);

    // Impl

    static final Tag<Ore> TAG = Tag.String("ore_id").map(Ore::fromNamespaceId, Ore::name);

    public @NotNull Block asBlock() {
        return oreBlock.withTag(TAG, this).withHandler(OreBlockHandler.instance());
    }

    public static @Nullable Ore fromBlock(@NotNull Block block) {
        return block.getTag(TAG);
    }


    public @NotNull LootTable lootTable() {
        final LootTable table = LootTable.fromNamespaceId(lootTableId());
        if (table != null)
            return table;

        // The loot table is missing, log an error and return an empty one.
        LOGGER.error("Loot table {} is missing, returning an empty one.", lootTableId());
        return LootTable.EMPTY;
    }


    // Registry

    public static final Codec<Ore> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(Ore::namespace),
            ExtraCodecs.BLOCK.fieldOf("oreBlock").forGetter(Ore::oreBlock),
            Codec.INT.fieldOf("health").forGetter(Ore::health),
            ExtraCodecs.NAMESPACE_ID.fieldOf("lootTable").forGetter(Ore::lootTableId)
    ).apply(i, Ore::new));

    public static final Registry<Ore> REGISTRY = Registry.codec("ore", CODEC);

    public static @UnknownNullability Ore fromNamespaceId(@NotNull NamespaceID namespace) {
        return REGISTRY.get(namespace);
    }

    public static @UnknownNullability Ore fromNamespaceId(@NotNull String namespace) {
        return REGISTRY.get(namespace);
    }

}
