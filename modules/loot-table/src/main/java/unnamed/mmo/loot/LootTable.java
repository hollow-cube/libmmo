package unnamed.mmo.loot;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.context.GenerationContext;

public interface LootTable {

    @NotNull String generate(@NotNull GenerationContext context);


}
