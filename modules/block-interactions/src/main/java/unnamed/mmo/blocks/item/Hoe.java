package unnamed.mmo.blocks.item;

import com.mojang.serialization.Codec;
import unnamed.mmo.item.ItemComponent;

public record Hoe() implements ItemComponent {

    public static final Codec<Hoe> CODEC = Codec.unit(Hoe::new);

}
