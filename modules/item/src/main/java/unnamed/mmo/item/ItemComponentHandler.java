package unnamed.mmo.item;

import com.mojang.serialization.Codec;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.registry.Resource;

public interface ItemComponentHandler<C extends ItemComponent> extends Resource {

    // Cannot be a method ref because REGISTRY may be mid-initialization when this is initialized.
    @SuppressWarnings("Convert2MethodRef")
    Codec<ItemComponentHandler<?>> CODEC = Codec.STRING.xmap(namespace -> ItemComponentRegistry.REGISTRY.get(namespace), ItemComponentHandler::name);


    // Descriptors

    @Override
    @NotNull NamespaceID namespace();

    @NotNull Class<C> componentType();

    @NotNull Codec<@NotNull C> codec();


    // Implementation

    default @Nullable EventNode<Event> eventNode() {
        return null;
    }

    //todo introduce method for modifying lore, but need to figure out priorities here.


    // Static helpers

    static @Nullable ItemComponentHandler<?> fromNamespaceId(@NotNull String namespace) {
        return ItemComponentRegistry.COMPONENT_ID_INDEX.get(namespace);
    }

    static <C extends ItemComponent> @NotNull ItemComponentHandler<C> from(C component) {
        //noinspection unchecked
        return (ItemComponentHandler<C>) from(component.getClass());
    }

    static <C extends ItemComponent> @NotNull ItemComponentHandler<C> from(Class<C> componentType) {
        var value = ItemComponentRegistry.COMPONENT_CLASS_INDEX.get(componentType);
        Check.notNull(value, "Missing component handler for component " + componentType);
        //noinspection unchecked
        return (ItemComponentHandler<C>) value;

    }

}
