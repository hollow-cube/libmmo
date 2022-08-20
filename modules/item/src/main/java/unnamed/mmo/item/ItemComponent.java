package unnamed.mmo.item;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public interface ItemComponent {

    Codec<ItemComponent> CODEC = ItemComponentHandler.CODEC.dispatch("type", ItemComponentHandler::from, ItemComponentHandler::codec);

}
