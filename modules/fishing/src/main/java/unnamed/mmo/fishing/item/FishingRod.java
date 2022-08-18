package unnamed.mmo.fishing.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import unnamed.mmo.item.ItemComponent;

public record FishingRod(
        double strength,
        int bobberCount
) implements ItemComponent {

    public static final Codec<FishingRod> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("strength").forGetter(FishingRod::strength),
            Codec.INT.fieldOf("bobberCount").forGetter(FishingRod::bobberCount)
    ).apply(i, FishingRod::new));

}
