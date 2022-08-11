package unnamed.mmo.loot.test;

import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.loot.LootPool;
import unnamed.mmo.loot.type.LootEntry;
import unnamed.mmo.loot.type.LootModifier;
import unnamed.mmo.loot.type.LootPredicate;

import java.util.ArrayList;
import java.util.List;

public class LootPoolBuilder {
    private final List<LootPredicate> predicates = new ArrayList<>();
    private final List<LootModifier> modifiers = new ArrayList<>();
    private final List<LootEntry<?>> entries = new ArrayList<>();
    private NumberProvider rolls = NumberProvider.constant(1);

    public LootPoolBuilder predicate(LootPredicate predicate) {
        predicates.add(predicate);
        return this;
    }

    public LootPoolBuilder modifier(LootModifier modifier) {
        modifiers.add(modifier);
        return this;
    }

    public LootPoolBuilder entry(LootEntry<?> entry) {
        entries.add(entry);
        return this;
    }

    public LootPoolBuilder rolls(int rolls) {
        this.rolls = NumberProvider.constant(rolls);
        return this;
    }

    public LootPoolBuilder rolls(NumberProvider rolls) {
        this.rolls = rolls;
        return this;
    }

    public LootPool build() {
        return new LootPool(predicates, modifiers, entries, rolls);
    }



}
