package unnamed.mmo.item;

import com.mojang.serialization.Codec;

/**
 * Base class for all {@link ItemComponent}s. See {@link ItemComponentHandler} for more information.
 */
public interface ItemComponent {

    Codec<ItemComponent> CODEC = ItemComponentHandler.CODEC.dispatch("type", ItemComponentHandler::from, ItemComponentHandler::codec);

}
