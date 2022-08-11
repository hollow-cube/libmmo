package unnamed.mmo.loot.type;

import com.mojang.serialization.Codec;

public interface LootEntry<T extends LootType> {

    Codec<LootEntry<?>> CODEC = ;

}
