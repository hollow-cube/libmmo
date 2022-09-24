package net.hollowcube.entity.task;

import com.mojang.serialization.Codec;
import net.hollowcube.entity.SmartEntity;
import net.hollowcube.registry.Resource;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.ResourceFactory;

public sealed interface Task permits AbstractTask {

    @NotNull State getState();

    void start(@NotNull SmartEntity entity);

    void tick(@NotNull SmartEntity entity, long time);

    enum State {
        INIT, RUNNING, COMPLETE, FAILED
    }

    interface Spec extends Resource {
        Codec<Spec> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);

        Registry<Spec> REGISTRY = Registry.codec("behaviors", CODEC);

        @NotNull Task create();
    }

    class Factory extends ResourceFactory<Task.Spec> {
        static final Registry<Factory> REGISTRY = Registry.service("task_factory", Factory.class);
        static final Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        @SuppressWarnings("Convert2MethodRef")
        //todo turn into Registry#required when updated
        public static final Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.get(ns), Factory::name);

        public Factory(String namespace, Class<? extends Task.Spec> type, Codec<? extends Task.Spec> codec) {
            super(namespace, type, codec);
        }

        static @NotNull Factory from(@NotNull Spec spec) {
            return TYPE_REGISTRY.get(spec.getClass());
        }
    }
}
