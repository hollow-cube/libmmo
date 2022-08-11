package unnamed.mmo.loot.type;

import com.mojang.serialization.Codec;

public interface LootModifier<T extends LootType> {

    Codec<LootModifier<?>> CODEC = ;

}
