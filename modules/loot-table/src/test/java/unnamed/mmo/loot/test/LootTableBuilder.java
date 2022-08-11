package unnamed.mmo.loot.test;

import net.minestom.server.utils.NamespaceID;
import unnamed.mmo.loot.LootPool;
import unnamed.mmo.loot.LootTable;
import unnamed.mmo.loot.type.LootModifier;

import java.util.ArrayList;
import java.util.List;

public class LootTableBuilder {
    private final List<LootModifier> modifiers = new ArrayList<>();
    private final List<LootPool> pools = new ArrayList<>();

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
