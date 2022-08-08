package unnamed.mmo.item;

import com.mojang.serialization.Codec;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.registry.Resource;

public interface ComponentHandler<C extends Component> extends Resource {
    Codec<ComponentHandler<?>> CODEC = Codec.STRING.xmap(ComponentRegistry.REGISTRY::get, ComponentHandler::name);


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

    static @Nullable ComponentHandler<?> fromNamespaceId(@NotNull String namespace) {
        return ComponentRegistry.COMPONENT_ID_INDEX.get(namespace);
    }

    static <C extends Component> @NotNull ComponentHandler<C> from(C component) {
        //noinspection unchecked
        return (ComponentHandler<C>) from(component.getClass());
    }

    static <C extends Component> @NotNull ComponentHandler<C> from(Class<C> componentType) {
        var value = ComponentRegistry.COMPONENT_CLASS_INDEX.get(componentType);
        Check.notNull(value, "Missing component handler for component " + componentType);
        //noinspection unchecked
        return (ComponentHandler<C>) value;

    }

}
