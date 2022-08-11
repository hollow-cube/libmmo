package unnamed.mmo.loot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import unnamed.mmo.loot.test.*;
import unnamed.mmo.loot.type.LootModifier;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static unnamed.mmo.loot.test.LootEntries.string;
import static unnamed.mmo.loot.test.LootEntries.strings;

public class TestLootPool {

    @Test
    public void testEmpty() {
        var pool = new LootPoolBuilder()
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = pool.generate(context);

        assertThat(result).isEmpty();
    }

    @Test
    public void testMultiItemOption() {
        var pool = new LootPoolBuilder()
                .entry(LootEntries.strings(1, "a", "b"))
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = pool.generate(context);

        assertThat(result).containsExactly("a", "b");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testConditionFailure(int value) {
        LootPoolBuilder builder = new LootPoolBuilder();
        for (int i = 0; i < value; i++)
            builder.predicate(LootPredicates.pass());
        builder.predicate(LootPredicates.fail());
        LootPool pool = builder
                .entry(string("a", 1))
                .build();
        var context = GenerationContexts.fixed(1);

        var result = pool.generate(context);

        // Never should have generated anything with a failing condition
        assertThat(result).isEmpty();
    }

    @Test
    public void testWeightedRolls() {
        var pool = new LootPoolBuilder()
                .entry(strings(1, "a"))
                .entry(strings(1, "b"))
                .build();

        var result1 = pool.generate(GenerationContexts.fixed(0));
        assertThat(result1)
                .containsExactly("a");

        var result2 = pool.generate(GenerationContexts.fixed(1));
        assertThat(result2)
                .containsExactly("b");
    }

    @Test
    public void testSingleModifier() {
        var pool = new LootPoolBuilder()
                .entry(LootEntries.strings(1, "a", "b"))
                .modifier(LootModifiers.stringRewrite("z"))
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = pool.generate(context);

        assertThat(result).containsExactly("z", "z");
    }

    @Test
    public void testMultiModifierOrder() {
        var pool = new LootPoolBuilder()
                .entry(LootEntries.strings(1, "a", "b"))
                .modifier(LootModifiers.stringRewrite("y"))
                .modifier(LootModifiers.stringRewrite("z"))
                .build();
        var context = GenerationContexts.fixed(1);

        List<Object> result = pool.generate(context);

        // y modifier should be executed first, leaving 'z's behind
        assertThat(result).containsExactly("z", "z");
    }


    //todo Codec tests
}
