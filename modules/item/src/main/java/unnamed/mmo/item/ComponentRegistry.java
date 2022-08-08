package unnamed.mmo.item;

import unnamed.mmo.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

class ComponentRegistry {

    static final Registry<ComponentHandler<?>> REGISTRY = Registry.manual(
            "component_handler",
            () -> {
                List<ComponentHandler<?>> handlers = new ArrayList<>();
                for (ComponentHandler<?> handler : ServiceLoader.load(ComponentHandler.class))
                    handlers.add(handler);
                return handlers;
            }
    );

    static final Registry.Index<String, ComponentHandler<?>> COMPONENT_ID_INDEX = REGISTRY.index(ComponentHandler::name);

    static final Registry.Index<Class<?>, ComponentHandler<?>> COMPONENT_CLASS_INDEX = REGISTRY.index(ComponentHandler::componentType);

}
