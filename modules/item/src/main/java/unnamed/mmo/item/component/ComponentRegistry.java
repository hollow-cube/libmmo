package unnamed.mmo.item.component;

import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

class ComponentRegistry {

    private static final Resource.Type RESOURCE = new Resource.Type("component_handlers");

    static final Map<Class<?>, String> CLASS_ID_MAP = new HashMap<>();
    static final Registry.Container<ComponentHandler<?>> CONTAINER = loadComponentHandlers();

    private static Registry.Container<ComponentHandler<?>> loadComponentHandlers() {
        Map<String, ComponentHandler<?>> resources = new HashMap<>();
        for (ComponentHandler<?> handler : ServiceLoader.load(ComponentHandler.class)) {
            resources.put(handler.name(), handler);
            CLASS_ID_MAP.put(handler.componentType(), handler.name());
        }
        return new Registry.Container<>(RESOURCE, resources);
    }

}
