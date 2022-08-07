package unnamed.mmo.item.component;

import com.mojang.serialization.Codec;

public interface ItemComponent {

    Codec<ItemComponent> CODEC = ComponentHandler.CODEC.dispatch("type", ComponentHandler::from, ComponentHandler::codec);


}
