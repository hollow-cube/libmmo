package unnamed.mmo.loot.type;

import com.mojang.serialization.Codec;

public interface LootPredicate<T extends LootType> {

    Codec<LootPredicate<?>> CODEC = ;

}
