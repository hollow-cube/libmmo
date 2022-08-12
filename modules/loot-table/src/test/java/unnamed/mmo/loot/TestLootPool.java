package unnamed.mmo.loot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import unnamed.mmo.loot.test.StringLootType;

import static com.google.common.truth.Truth.assertThat;
import static unnamed.mmo.loot.test.LootTableUtil.*;

public class TestLootPool {

    @Test
    public void testEmpty() {
        var pool = pool()
                .build();

        var result = pool.generate(context(1));

        assertThat(result).isEmpty();
    }

    @Test
    public void testMultiItemOption() {
        var pool = pool()
                .entry(StringLootType.entries(1, "a", "b"))
                .build();

        var result = pool.generate(context(1));

        assertThat(result).containsExactly("a", "b");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testConditionFailure(int value) {
        LootPoolBuilder builder = pool();
        for (int i = 0; i < value; i++)
            builder.predicate(passPredicate());
        builder.predicate(failPredicate());
        LootPool pool = builder
                .entry(StringLootType.entry(1, "a"))
                .build();

        var result = pool.generate(context(1));

        // Never should have generated anything with a failing condition
        assertThat(result).isEmpty();
    }

    @Test
    public void testWeightedRolls() {
        var pool = pool()
                .entry(StringLootType.entry(1, "a"))
                .entry(StringLootType.entry(1, "b"))
                .build();

        var result1 = pool.generate(context(0));
        assertThat(result1)
                .containsExactly("a");

        var result2 = pool.generate(context(1));
        assertThat(result2)
                .containsExactly("b");
    }

    @Test
    public void testSingleModifier() {
        var pool = pool()
                .entry(StringLootType.entries(1, "a", "b"))
                .modifier(StringLootType.rewrite("z"))
                .build();

        var result = pool.generate(context(1));

        assertThat(result).containsExactly("z", "z");
    }

    @Test
    public void testMultiModifierOrder() {
        var pool = pool()
                .entry(StringLootType.entries(1, "a", "b"))
                .modifier(StringLootType.rewrite("y"))
                .modifier(StringLootType.rewrite("z"))
                .build();

        var result = pool.generate(context(1));

        // y modifier should be executed first, leaving 'z's behind
        assertThat(result).containsExactly("z", "z");
    }


    //todo Codec tests
}
