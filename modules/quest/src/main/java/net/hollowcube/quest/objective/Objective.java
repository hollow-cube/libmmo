package net.hollowcube.quest.objective;

import com.mojang.serialization.Codec;
import net.kyori.adventure.text.Component;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.quest.QuestContext;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.ResourceFactory;

import java.util.concurrent.CompletableFuture;

import static net.hollowcube.dfu.ExtraCodecs.lazy;

public interface Objective {

    Codec<Objective> CODEC = lazy(() -> Factory.CODEC).dispatch(Factory::from, Factory::codec);

    @NotNull CompletableFuture<Void> onStart(@NotNull QuestContext context);

    @Nullable Component getCurrentStatus(@NotNull QuestContext context);


    class Factory extends ResourceFactory<Objective> {
        public static Registry<Factory> REGISTRY = Registry.service("quest_objective", Factory.class);

        public static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        static final Codec<Factory> CODEC = Codec.STRING.xmap(namespace -> REGISTRY.required(namespace), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends Objective> type, Codec<? extends Objective> codec) {
            super(namespace, type, codec);
        }

        static @Nullable Factory from(@NotNull Objective objective) {
            return TYPE_REGISTRY.get(objective.getClass());
        }
    }
}
