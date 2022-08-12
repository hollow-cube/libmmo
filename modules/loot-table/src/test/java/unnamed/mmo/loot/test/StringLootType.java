package unnamed.mmo.loot.test;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.context.GenerationContext;
import unnamed.mmo.loot.type.LootEntry;
import unnamed.mmo.loot.type.LootModifier;

import java.util.List;

public class StringLootType {

    public static LootEntry<?> entry(int weight, String... values) {
        return new StringLootEntry(false, weight, List.of(values));
    }

    public static LootEntry<?> entries(int weight, String... values) {
        return new StringLootEntry(true, weight, List.of(values));
    }

    public static LootModifier rewrite(String newValue) {
        return new StringRewriteModifier(newValue);
    }

    record StringLootEntry(
            boolean multiple,
            int weight,
            List<String> strings
    ) implements LootEntry<String> {

        @Override
        public @NotNull List<@NotNull Option<String>> generate(@NotNull GenerationContext context) {
            if (multiple) return List.of(new Option<>(strings(), weight));
            return strings().stream().map(s -> new Option<>(List.of(s), weight)).toList();
        }
    }

    record StringRewriteModifier(
            String newValue
    ) implements LootModifier {

        @Override
        public @NotNull Object apply(@NotNull Object input) {
            if (input instanceof String)
                return newValue;
            return input;
        }
    }

}
