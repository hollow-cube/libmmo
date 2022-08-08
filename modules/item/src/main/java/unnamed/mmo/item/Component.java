package unnamed.mmo.item;

import com.mojang.serialization.Codec;

public interface Component {

    Codec<Component> CODEC = ComponentHandler.CODEC.dispatch("type", ComponentHandler::from, ComponentHandler::codec);


}
