package unnamed.mmo.loot.test;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.context.GenerationContext;
import unnamed.mmo.loot.type.LootEntry;

import java.util.List;

public class LootEntries {

    public static LootEntry<String> string(String value, int weight) {
        return new LootEntry<String>() {
            @Override
            public @NotNull List<@NotNull Option> generate(@NotNull GenerationContext context) {
                return List.of(new Option(List.of(value), weight));
            }
        };
    }
}
