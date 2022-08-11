package unnamed.mmo.loot.type;

import unnamed.mmo.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class LootTypeRegistry {

    public static final Registry<LootType<?>> REGISTRY = Registry.manual("loot_type", () -> {
        List<LootType<?>> registry = new ArrayList<>();
        for (LootType<?> entry : ServiceLoader.load(LootType.class))
            registry.add(entry);
        return registry;
    });

    public static final Registry<LootPredicate<?>> PREDICATES = Registry.manual("loot_predicate", () -> {
        List<LootPredicate<?>> registry = new ArrayList<>();
        for (LootPredicate<?> entry : ServiceLoader.load(LootPredicate.class))
            registry.add(entry);
        return registry;
    });

}
