package unnamed.mmo.loot;

import org.junit.jupiter.api.Test;
import unnamed.mmo.loot.test.StringLootType;

import static com.google.common.truth.Truth.assertThat;
import static unnamed.mmo.loot.test.LootTableUtil.*;

public class TestLootTable {

    @Test
    public void testEmpty() {
        var table = table().build();

        var result = table.generate(context(1));

        assertThat(result.results())
                .isEmpty();
    }

    @Test
    public void testSinglePool() {
        var table = table()
                .pool(pool()
                        .entry(StringLootType.entry(1, "a"))
                        .build())
                .build();

        var result = table.generate(context(1));

        assertThat(result.results())
                .containsExactly("a");
    }

    @Test
    public void testMultiPool() {
        var table = table()
                .pool(pool()
                        .entry(StringLootType.entry(1, "a"))
                        .build())
                .pool(pool()
                        .entry(StringLootType.entry(1, "b"))
                        .build())
                .build();

        var result = table.generate(context(1));

        assertThat(result.results())
                .containsExactly("a", "b");
    }

    @Test
    public void testModifyAllResults() {
        var table = table()
                .modifier(StringLootType.rewrite("zzz"))
                .pool(pool()
                        .entry(StringLootType.entry(1, "a"))
                        .build())
                .pool(pool()
                        .entry(StringLootType.entry(1, "b"))
                        .build())
                .build();

        var result = table.generate(context(1));

        assertThat(result.results())
                .containsExactly("zzz", "zzz");
    }


    //todo Codec test i guess
}
