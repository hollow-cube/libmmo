package unnamed.mmo.blocks.item;

import com.mojang.serialization.Codec;
import unnamed.mmo.item.ItemComponent;

public record HoeComponent() implements ItemComponent {

    public static final Codec<HoeComponent> CODEC = Codec.unit(HoeComponent::new);

}
