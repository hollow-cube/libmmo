package unnamed.mmo.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.loot.context.GenerationContext;
import unnamed.mmo.loot.type.LootEntry;
import unnamed.mmo.loot.type.LootModifier;
import unnamed.mmo.loot.type.LootPredicate;

import java.util.ArrayList;
import java.util.List;

public record LootPool(
        @NotNull List<LootPredicate> conditions,
        @NotNull List<LootModifier> modifiers,
        @NotNull List<LootEntry<?>> entries,
        @NotNull NumberProvider rolls
) {

    public static final Codec<LootPool> CODEC = RecordCodecBuilder.create(i -> i.group(
            LootPredicate.CODEC.listOf().fieldOf("conditions").forGetter(LootPool::conditions),
            LootModifier.CODEC.listOf().fieldOf("modifiers").forGetter(LootPool::modifiers),
            LootEntry.CODEC.listOf().fieldOf("entries").forGetter(LootPool::entries),
            NumberProvider.CODEC.fieldOf("rolls").forGetter(LootPool::rolls)
    ).apply(i, LootPool::new));

    public @NotNull List<@NotNull Object> generate(@NotNull GenerationContext context) {
        // Ensure all conditions match
        for (LootPredicate condition : conditions()) {
            if (!condition.test(context)) return List.of();
        }

        // Generate available entries
        var options = entries()
                .stream()
                .map(entry -> entry.generate(context))
                .flatMap(List::stream)
                .toList();
        int totalWeight = options.stream()
                .mapToInt(LootEntry.Option::weight).sum();

        // Roll for entries
        List<@NotNull Object> output = new ArrayList<>();
        for (int i = 0; i < rolls().nextLong(context); i++) {
            int roll = (int) (context.random() * totalWeight);
            for (LootEntry.Option<?> option : options) {
                roll -= option.weight();
                if (roll <= 0) {
                    output.addAll(option.loot());
                    break;
                }
            }
        }

        // Apply modifiers to final entries
        return output.stream()
                .map(entry -> {
                    for (LootModifier modifier : modifiers())
                        entry = modifier.apply(entry);
                    return entry;
                })
                .toList();
    }

}
