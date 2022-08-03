package unnamed.mmo.item.component;

import com.google.gson.JsonObject;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.registry.Resource;

import java.util.function.Function;

import static net.minestom.server.registry.Registry.Properties;

public interface ComponentHandler<C extends ItemComponent> extends Resource {



    @NotNull EventNode<Event> eventNode();

    @NotNull Class<C> componentType();

    @NotNull Function<@NotNull Properties, @NotNull C> factory();


    // Static helpers

    static @Nullable String getComponentId(Class<? extends ItemComponent> componentClass) {
        return ComponentRegistry.CLASS_ID_MAP.get(componentClass);
    }

    static <C extends ItemComponent> @Nullable ComponentHandler<C> fromNamespaceId(@NotNull String namespace) {
        //noinspection unchecked
        return (ComponentHandler<C>) ComponentRegistry.CONTAINER.getSafe(namespace);
    }
}
