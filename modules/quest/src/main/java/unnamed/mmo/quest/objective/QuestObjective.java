package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.ResourceFactory;

import java.util.concurrent.CompletableFuture;

public interface QuestObjective {

    CompletableFuture<Void> onStart(QuestContext context);



    class Factory extends ResourceFactory<QuestObjective> {
        static Registry<Factory> REGISTRY = Registry.service("quest_objective", Factory.class);

        static Registry.Index<Class<?>, Factory> index = REGISTRY.index(Factory::type);

        static final Codec<Factory> CODEC = Codec.STRING.xmap(namespace -> REGISTRY.get(namespace), Factory::name);

        static Factory from(QuestObjective objective) {
            return null;
        }
        public Factory(NamespaceID namespace, Class<? extends QuestObjective> type, Codec<? extends QuestObjective> codec) {
            super(namespace, type, codec);
        }
    }

    Codec<QuestObjective> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);
}
