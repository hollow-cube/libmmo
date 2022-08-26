package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import net.kyori.adventure.text.Component;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.ResourceFactory;

import java.util.concurrent.CompletableFuture;

import static unnamed.mmo.util.ExtraCodecs.lazy;

public interface QuestObjective {

    Codec<QuestObjective> CODEC = lazy(() -> Factory.CODEC).dispatch(Factory::from, Factory::codec);

    @NotNull CompletableFuture<Void> onStart(@NotNull QuestContext context);

    @Nullable Component getCurrentStatus(@NotNull QuestContext context);


    class Factory extends ResourceFactory<QuestObjective> {
        public static Registry<Factory> REGISTRY = Registry.service("quest_objective", Factory.class);

        public static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        static final Codec<Factory> CODEC = Codec.STRING.xmap(namespace -> REGISTRY.required(namespace), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends QuestObjective> type, Codec<? extends QuestObjective> codec) {
            super(namespace, type, codec);
        }

        static @Nullable Factory from(@NotNull QuestObjective objective) {
            return TYPE_REGISTRY.get(objective.getClass());
        }
    }
}
