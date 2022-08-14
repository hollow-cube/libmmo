package unnamed.mmo.loot.test;

import com.google.auto.service.AutoService;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.LootResult;
import unnamed.mmo.loot.LootContext;
import unnamed.mmo.loot.LootEntry;
import unnamed.mmo.loot.LootModifier;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

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
        public @NotNull List<@NotNull Option<String>> generate(@NotNull LootContext context) {
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

    @AutoService(LootResult.DefaultDistributor.class)
    public static class StringDistributor implements LootResult.DefaultDistributor<String> {
        // Contains all distributed strings since the test started. If a string is applied twice, it is an error.
        public static final List<String> DISTRIBUTED_STRINGS = new ArrayList<>();


        @Override
        public @NotNull NamespaceID namespace() {
            return NamespaceID.from("test:string");
        }

        @Override
        public @NotNull Class<String> type() {
            return String.class;
        }

        @Override
        public void apply(@NotNull LootContext context, @NotNull String s) {
            // Cannot use the same string twice
            assertThat(DISTRIBUTED_STRINGS).doesNotContain(s);
            DISTRIBUTED_STRINGS.add(s);
        }
    }

}
