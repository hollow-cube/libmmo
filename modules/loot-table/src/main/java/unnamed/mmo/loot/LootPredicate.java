package unnamed.mmo.loot;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.ResourceFactory;

import java.util.Collection;

public interface LootPredicate {
    Codec<LootPredicate> CODEC = Factory.CODEC.dispatch(Factory::from, Factory::codec);

    static boolean all(@NotNull LootContext context, @NotNull Collection<LootPredicate> conditions) {
        for (LootPredicate condition : conditions) {
            if (!condition.test(context)) return false;
        }
        return true;
    }

    boolean test(@NotNull LootContext context);


    abstract class Factory extends ResourceFactory<LootPredicate> {
        static Registry<Factory> REGISTRY = Registry.service("loot_predicates", LootPredicate.Factory.class);
        static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        public static final Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.get(ns), Factory::name);

        public Factory(NamespaceID namespace, Class<? extends LootPredicate> type, Codec<? extends LootPredicate> codec) {
            super(namespace, type, codec);
        }

        static @NotNull Factory from(@NotNull LootPredicate predicate) {
            return TYPE_REGISTRY.get(predicate.getClass());
        }
    }
}
