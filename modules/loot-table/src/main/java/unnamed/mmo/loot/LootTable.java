package unnamed.mmo.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.impl.LootResultImpl;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;
import java.util.stream.Collectors;

public record LootTable(
        @NotNull NamespaceID namespace,
        @NotNull List<LootModifier> modifiers,
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

    public @NotNull LootResult generate(@NotNull LootContext context) {
        return new LootResultImpl(pools.stream()
                .map(pool -> pool.generate(context))
                .flatMap(List::stream)
                .map(this::applyModifiers)
                .collect(Collectors.toList()));
    }

    private @NotNull Object applyModifiers(@NotNull Object input) {
        for (LootModifier modifier : modifiers())
            input = modifier.apply(input);
        return input;
    }

}
