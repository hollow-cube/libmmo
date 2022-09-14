package net.hollowcube.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.dfu.ExtraCodecs;
import net.hollowcube.loot.impl.LootResultImpl;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.Resource;

import java.util.List;
import java.util.stream.Collectors;

public record LootTable(
        @NotNull NamespaceID namespace,
        @NotNull List<LootModifier> modifiers,
        @NotNull List<LootPool> pools
) implements Resource {

    public static final LootTable EMPTY = new LootTable(NamespaceID.from("starlight:empty"), List.of(), List.of());

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


    // Registry

    public static final Codec<LootTable> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(LootTable::namespace),
            LootModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(LootTable::modifiers),
            LootPool.CODEC.listOf().optionalFieldOf("pools", List.of()).forGetter(LootTable::pools)
    ).apply(i, LootTable::new));

    public static final Registry<LootTable> REGISTRY = Registry.codec("loot_table", CODEC);

    public static @Nullable LootTable fromNamespaceId(@NotNull NamespaceID namespace) {
        return REGISTRY.get(namespace);
    }

    public static @Nullable LootTable fromNamespaceId(@NotNull String namespace) {
        return REGISTRY.get(namespace);
    }

}
