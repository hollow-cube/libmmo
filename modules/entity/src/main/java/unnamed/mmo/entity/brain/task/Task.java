package unnamed.mmo.entity.brain.task;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.ResourceFactory;

public sealed interface Task permits AbstractTask {
    Codec<Task> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);

    @NotNull State getState();

    void start(@NotNull Brain brain);

    void tick(@NotNull Brain brain);

    @NotNull Task deepCopy();

    enum State {
        INIT, RUNNING, COMPLETE, FAILED
    }

    class Factory extends ResourceFactory<Task> {
        static final Registry<Factory> REGISTRY = Registry.service("task_factory", Factory.class);
        static final Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        @SuppressWarnings("Convert2MethodRef")
        public static final Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.get(ns), Factory::name);

        public Factory(String namespace, Class<? extends Task> type, Codec<? extends Task> codec) {
            super(namespace, type, codec);
        }

        static @NotNull Factory from(@NotNull Task task) {
            return TYPE_REGISTRY.get(task.getClass());
        }
    }
}
