package unnamed.mmo.loot.test;

import unnamed.mmo.loot.type.LootPredicate;

public class LootPredicates {

    public static LootPredicate pass() {
        return ignored -> true;
    }

    public static LootPredicate fail() {
        return ignored -> false;
    }

}
