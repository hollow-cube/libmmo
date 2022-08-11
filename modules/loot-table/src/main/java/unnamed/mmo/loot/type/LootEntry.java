package unnamed.mmo.loot.type;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LootEntry<T> {

    Codec<LootEntry<?>> CODEC = ;

    int weight();

    @NotNull List<LootPredicate<T>> conditions();

}
