package unnamed.mmo.loot.test;

import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.loot.LootPool;
import unnamed.mmo.loot.LootTable;
import unnamed.mmo.loot.context.GenerationContext;
import unnamed.mmo.loot.type.LootEntry;
import unnamed.mmo.loot.type.LootModifier;
import unnamed.mmo.loot.type.LootPredicate;

import java.util.ArrayList;
import java.util.List;

public class LootTableUtil {

    public static LootTableBuilder table() {
        return new LootTableBuilder();
    }

    public static LootPoolBuilder pool() {
        return new LootPoolBuilder();
    }

    public static GenerationContext context(double... values) {
        Check.argCondition(values.length == 0, "Empty context");
        return new GenerationContext() {
            int i = 0;

            @Override
            public double random() {
                double value = values[i];
                if (i < values.length - 1)
                    i++;
                return value;
            }
        };
    }

    public static LootPredicate passPredicate() {
        return ignored -> true;
    }

    public static LootPredicate failPredicate() {
        return ignored -> false;
    }

    public static class LootTableBuilder {
        private final List<LootModifier> modifiers = new ArrayList<>();
        private final List<LootPool> pools = new ArrayList<>();

        private LootTableBuilder() {

        }

        public LootTableBuilder modifier(LootModifier modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public LootTableBuilder pool(LootPool pool) {
            this.pools.add(pool);
            return this;
        }

        public LootTable build() {
            return new LootTable(NamespaceID.from("test"), modifiers, pools);
        }
    }

    public static class LootPoolBuilder {
        private final List<LootPredicate> predicates = new ArrayList<>();
        private final List<LootModifier> modifiers = new ArrayList<>();
        private final List<LootEntry<?>> entries = new ArrayList<>();
        private NumberProvider rolls = NumberProvider.constant(1);

        private LootPoolBuilder() {

        }

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

}
