package unnamed.mmo.loot.impl;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.LootContext;
import unnamed.mmo.loot.LootEntry;
import unnamed.mmo.loot.LootPredicate;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;

public record GroupLootEntry(
        @NotNull List<LootPredicate> conditions,
        @NotNull List<LootEntry<?>> children
) implements LootEntry<Object> {

    public static final Codec<GroupLootEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
            LootPredicate.CODEC.listOf().fieldOf("conditions").forGetter(GroupLootEntry::conditions),
            // Recursive codec, so must use a lazy version
            ExtraCodecs.lazy(() -> LootEntry.CODEC).listOf().fieldOf("children").forGetter(GroupLootEntry::children)
    ).apply(i, GroupLootEntry::new));

    @Override
    public @NotNull List<@NotNull Option<Object>> generate(@NotNull LootContext context) {
        // Ensure all conditions match
        for (LootPredicate condition : conditions()) {
            if (!condition.test(context)) return List.of();
        }

        // Collect all results
        //noinspection unchecked
        return children()
                .stream()
                .map(entry -> entry.generate(context))
                .flatMap(List::stream)
                // This cast is required to make types work out here
                .map(it -> (Option<Object>) it)
                .toList();
    }


    @AutoService(LootEntry.Factory.class)
    public static class Factory extends LootEntry.Factory {

        public Factory() {
            super(
                    NamespaceID.from("unnamed:group"),
                    GroupLootEntry.class,
                    GroupLootEntry.CODEC
            );
        }
    }

}
