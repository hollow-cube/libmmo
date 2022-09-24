package net.hollowcube.item;

import com.mojang.serialization.Codec;
import net.hollowcube.registry.Resource;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemComponentHandler<C extends ItemComponent> extends Resource {

    // Cannot be a method ref because REGISTRY may be mid-initialization when this is initialized.
    @SuppressWarnings("Convert2MethodRef")
    Codec<ItemComponentHandler<?>> CODEC = Codec.STRING.xmap(namespace -> ItemComponentRegistry.REGISTRY.required(namespace), ItemComponentHandler::name);


    // Descriptors

    @Override
    @NotNull NamespaceID namespace();

    @NotNull Class<C> componentType();

    @NotNull Codec<@NotNull C> codec();

    /**
     * The priority of this component when building the {@link ItemStack}. Relevant when applying lore.
     */
    default int priority() {
        return 0;
    }


    // Implementation

    /**
     * The returned event node (if present) is registered to the server. This should be used to implement any behavior
     * required by this component.
     * <p>
     * todo would be nice to have some utility functions instead of having to manually
     *      filter and parse the item each time.
     */
    default @Nullable EventNode<Event> eventNode() {
        return null;
    }

    /**
     * Called when an {@link ItemStack} is being constructed from the associated item.
     * <p>
     * For now, only lore elements should be appended. In the future, this may also be used to manage transient data
     * such as durability, debug tool type, etc.
     *
     * @param component The instance of this component
     * @param builder   The item builder to modify
     */
    default void buildItemStack(@NotNull C component, @NotNull ItemStack.Builder builder) {}


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
