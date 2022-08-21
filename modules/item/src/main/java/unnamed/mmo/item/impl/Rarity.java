package unnamed.mmo.item.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import unnamed.mmo.item.ItemComponent;

public record Rarity(
        String value
) implements ItemComponent {

    public static final Codec<Rarity> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("value").forGetter(Rarity::value)
    ).apply(i, Rarity::new));

}
