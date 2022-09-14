package net.hollowcube.item;

import net.hollowcube.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@ApiStatus.Internal
class ItemComponentRegistry {

    static final Registry<ItemComponentHandler<?>> REGISTRY = Registry.manual(
            "component_handler",
            () -> {
                List<ItemComponentHandler<?>> handlers = new ArrayList<>();
                for (ItemComponentHandler<?> handler : ServiceLoader.load(ItemComponentHandler.class))
                    handlers.add(handler);
                return handlers;
            }
    );

    static final Registry.Index<String, ItemComponentHandler<?>> COMPONENT_ID_INDEX = REGISTRY.index(ItemComponentHandler::name);

    static final Registry.Index<Class<?>, ItemComponentHandler<?>> COMPONENT_CLASS_INDEX = REGISTRY.index(ItemComponentHandler::componentType);

}
