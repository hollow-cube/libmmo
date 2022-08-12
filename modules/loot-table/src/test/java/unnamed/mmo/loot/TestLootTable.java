package unnamed.mmo.loot;

import org.junit.jupiter.api.Test;
import unnamed.mmo.loot.test.*;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static unnamed.mmo.loot.test.LootTableUtil.*;

public class TestLootTable {

    @Test
    public void testEmpty() {
        var table = table().build();

        List<Object> result = table.generate(context(1));

        assertThat(result)
                .isEmpty();
    }

    @Test
    public void testSinglePool() {
        var table = table()
                .pool(pool()
                        .entry(StringLootType.entry(1, "a"))
                        .build())
                .build();

        List<Object> result = table.generate(context(1));

        assertThat(result)
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

        List<Object> result = table.generate(context(1));

        assertThat(result)
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

        List<Object> result = table.generate(context(1));

        assertThat(result)
                .containsExactly("zzz", "zzz");
    }


    //todo Codec test i guess
}
