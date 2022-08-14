package unnamed.mmo.loot;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.registry.Resource;

import java.util.Collection;

public interface LootResult {

    @NotNull Collection<Object> results();

    <T> void override(@NotNull Class<T> type, @NotNull Distributor<T> distributor);

    void apply(@NotNull LootContext context);


    @FunctionalInterface
    interface Distributor<T> {

        void apply(@NotNull LootContext context, @NotNull T t);

    }

    interface DefaultDistributor<T> extends Distributor<T>, Resource {

        @Override
        @NotNull NamespaceID namespace();

        @NotNull Class<T> type();
    }

}
