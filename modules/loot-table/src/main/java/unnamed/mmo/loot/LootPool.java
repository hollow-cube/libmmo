package unnamed.mmo.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.loot.type.LootEntry;
import unnamed.mmo.loot.type.LootModifier;
import unnamed.mmo.loot.type.LootPredicate;

import java.util.List;

public record LootPool(
        @NotNull List<LootPredicate<?>> conditions,
        @NotNull List<LootModifier<?>> modifiers,
        @NotNull List<LootEntry<?>> entries,
        @NotNull NumberProvider rolls
) {

    public static final Codec<LootPool> CODEC = RecordCodecBuilder.create(i -> i.group(
            LootPredicate.CODEC.listOf().fieldOf("conditions").forGetter(LootPool::conditions),
            LootModifier.CODEC.listOf().fieldOf("modifiers").forGetter(LootPool::modifiers),
            LootEntry.CODEC.listOf().fieldOf("entries").forGetter(LootPool::entries),
            NumberProvider.CODEC.fieldOf("rolls").forGetter(LootPool::rolls)
    ).apply(i, LootPool::new));

}
