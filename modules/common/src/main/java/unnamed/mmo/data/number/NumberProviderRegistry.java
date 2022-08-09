package unnamed.mmo.data.number;

import unnamed.mmo.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

final class NumberProviderRegistry {

    static Registry<NumberProvider.Factory<?>> FACTORIES = Registry.manual("number_providers", () -> {
        List<NumberProvider.Factory<?>> registry = new ArrayList<>();
        for (NumberProvider.Factory<?> factory : ServiceLoader.load(NumberProvider.Factory.class)) {
            registry.add(factory);
        }
        return registry;
    });

    static Registry.Index<Class<?>, NumberProvider.Factory<?>> FACTORIES_BY_TYPE = FACTORIES.index(NumberProvider.Factory::type);



}
