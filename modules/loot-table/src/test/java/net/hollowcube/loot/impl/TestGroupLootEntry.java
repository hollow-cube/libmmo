package net.hollowcube.loot.impl;

import org.junit.jupiter.api.Test;
import net.hollowcube.loot.test.StringLootType;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static net.hollowcube.loot.test.LootTableUtil.context;
import static net.hollowcube.loot.test.LootTableUtil.failPredicate;

public class TestGroupLootEntry {

    @Test
    public void testEmpty() {
        var entry = new GroupLootEntry(
                List.of(),
                List.of()
        );

        var result = entry.generate(context(1));

        assertThat(result).isEmpty();
    }

    @Test
    public void testConditionFail() {
        var entry = new GroupLootEntry(
                List.of(failPredicate()),
                List.of(StringLootType.entry(1, ""))
        );

        var result = entry.generate(context(1));

        // Even though there is an entry, it should not be returned
        assertThat(result).isEmpty();
    }

    @Test
    public void testAllOptionsReturned() {
        var entry = new GroupLootEntry(
                List.of(),
                List.of(
                        StringLootType.entry(1, "a", "b"),
                        StringLootType.entries(1, "c", "d")
                )
        );

        var result = entry.generate(context(1));
        var collected = result.stream()
                .flatMap(o -> o.loot().stream())
                .toList();

        assertThat(collected)
                .containsExactly("a", "b", "c", "d");
    }

}
