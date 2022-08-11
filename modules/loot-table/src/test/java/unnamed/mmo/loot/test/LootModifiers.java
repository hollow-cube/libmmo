package unnamed.mmo.loot.test;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.type.LootModifier;

public class LootModifiers {

    public static @NotNull LootModifier stringRewrite(@NotNull String newValue) {
        return old -> {
            if (old instanceof String)
                return newValue;
            return old;
        };
    }
}
