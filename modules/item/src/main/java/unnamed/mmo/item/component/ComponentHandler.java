package unnamed.mmo.item.component;

import com.mojang.serialization.Codec;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.registry.Resource;

import java.util.function.Function;

import static net.minestom.server.registry.Registry.Properties;

public interface ComponentHandler<C extends ItemComponent> extends Resource {

    Codec<ComponentHandler<?>> CODEC = Codec.STRING.xmap(ComponentRegistry.REGISTRY::get, ComponentHandler::name);

    @NotNull EventNode<Event> eventNode();

    @NotNull Class<C> componentType();

    @NotNull Function<@NotNull Properties, @NotNull C> factory();

    @NotNull Codec<@NotNull C> codec();


    // Static helpers

    static @Nullable ComponentHandler<?> fromNamespaceId(@NotNull String namespace) {
        return ComponentRegistry.COMPONENT_ID_INDEX.get(namespace);
    }

    static <C extends ItemComponent> @NotNull ComponentHandler<C> from(C component) {
        //noinspection unchecked
        return (ComponentHandler<C>) from(component.getClass());
    }

    static <C extends ItemComponent> @NotNull ComponentHandler<C> from(Class<C> componentType) {
        var value = ComponentRegistry.COMPONENT_CLASS_INDEX.get(componentType);
        Check.notNull(value, "Missing component handler for component " + componentType);
        //noinspection unchecked
        return (ComponentHandler<C>) value;

    }

}
