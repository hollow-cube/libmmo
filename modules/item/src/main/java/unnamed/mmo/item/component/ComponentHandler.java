package unnamed.mmo.item.component;

import com.mojang.serialization.Codec;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
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

    static <C extends ItemComponent> @NotNull ComponentHandler<C> from(C component) {
        var value = ComponentRegistry.COMPONENT_CLASS_INDEX.get(component.getClass());
        Check.notNull(value, "Missing component handler for component " + component.getClass());
        //noinspection unchecked
        return (ComponentHandler<C>) value;
    }

}
