package unnamed.mmo.loot;

import org.junit.jupiter.api.Test;
import unnamed.mmo.loot.test.*;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestLootTable {

    @Test
    public void testEmpty() {
        var table = new LootTableBuilder()
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = table.generate(context);

        assertThat(result)
                .isEmpty();
    }

    @Test
    public void testSinglePool() {
        var table = new LootTableBuilder()
                .pool(new LootPoolBuilder()
                        .entry(LootEntries.string("a", 1))
                        .build())
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = table.generate(context);

        assertThat(result)
                .containsExactly("a");
    }

    @Test
    public void testMultiPool() {
        var table = new LootTableBuilder()
                .pool(new LootPoolBuilder()
                        .entry(LootEntries.string("a", 1))
                        .build())
                .pool(new LootPoolBuilder()
                        .entry(LootEntries.string("b", 1))
                        .build())
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = table.generate(context);

        assertThat(result)
                .containsExactly("a", "b");
    }

    @Test
    public void testModifyAllResults() {
        var table = new LootTableBuilder()
                .modifier(LootModifiers.stringRewrite("zzz"))
                .pool(new LootPoolBuilder()
                        .entry(LootEntries.string("a", 1))
                        .build())
                .pool(new LootPoolBuilder()
                        .entry(LootEntries.string("b", 1))
                        .build())
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = table.generate(context);

        assertThat(result)
                .containsExactly("zzz", "zzz");
    }


    //todo Codec test i guess
}
