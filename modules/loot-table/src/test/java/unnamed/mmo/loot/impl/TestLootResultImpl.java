package unnamed.mmo.loot.impl;

import org.junit.jupiter.api.Test;
import unnamed.mmo.loot.test.StringLootType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.google.common.truth.Truth.assertThat;
import static unnamed.mmo.loot.test.LootTableUtil.context;

public class TestLootResultImpl {

    @Test
    public void testDefaultDistributor() {
        LootResultImpl lootResult = new LootResultImpl(List.of("testDefaultDistributor"));
        lootResult.apply(context(1)).join();

        assertThat(StringLootType.StringDistributor.DISTRIBUTED_STRINGS)
                .contains("testDefaultDistributor");
    }

    @Test
    public void testOverrideDistributor() {
        LootResultImpl lootResult = new LootResultImpl(List.of("testOverrideDistributor"));
        lootResult.override(String.class, (context, value) -> {
            assertThat(value).isEqualTo("testOverrideDistributor");
            StringLootType.StringDistributor.DISTRIBUTED_STRINGS.add(value + "2");
            return CompletableFuture.completedFuture(null);
        });

        lootResult.apply(context(1)).join();

        assertThat(StringLootType.StringDistributor.DISTRIBUTED_STRINGS)
                .doesNotContain("testOverrideDistributor");
        assertThat(StringLootType.StringDistributor.DISTRIBUTED_STRINGS)
                .contains("testOverrideDistributor2");
    }
}
