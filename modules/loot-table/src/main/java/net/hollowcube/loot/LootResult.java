package net.hollowcube.loot;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.registry.Resource;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface LootResult {

    @NotNull Collection<Object> results();

    int size();

    <T> void override(@NotNull Class<T> type, @NotNull Distributor<T> distributor);

    @NotNull CompletableFuture<Void> apply(@NotNull LootContext context);


    @FunctionalInterface
    interface Distributor<T> {

        @NotNull CompletableFuture<Void> apply(@NotNull LootContext context, @NotNull T t);

    }

    interface DefaultDistributor<T> extends Distributor<T>, Resource {

        @Override
        @NotNull NamespaceID namespace();

        @NotNull Class<T> type();
    }

}
