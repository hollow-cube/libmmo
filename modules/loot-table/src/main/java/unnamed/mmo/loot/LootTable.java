package unnamed.mmo.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.context.GenerationContext;
import unnamed.mmo.loot.type.LootModifier;
import unnamed.mmo.loot.type.LootType;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;

public record LootTable(
        @NotNull NamespaceID namespace,
        @NotNull List<LootModifier<?>> modifiers,
        @NotNull List<LootPool> pools
) implements Resource {

    public static final Codec<LootTable> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(LootTable::namespace),
            LootModifier.CODEC.listOf().fieldOf("modifiers").forGetter(LootTable::modifiers),
            LootPool.CODEC.listOf().fieldOf("pools").forGetter(LootTable::pools)
    ).apply(i, LootTable::new));

    public LootTable {
        modifiers = List.copyOf(modifiers);
        pools = List.copyOf(pools);
    }

    public @NotNull List<LootType> generate(@NotNull GenerationContext context) {
        return List.of();
    }


}
