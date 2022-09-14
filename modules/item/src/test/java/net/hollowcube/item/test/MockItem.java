package net.hollowcube.item.test;

import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.item.Item;
import net.hollowcube.item.ItemComponent;

import java.util.Map;
import java.util.stream.Stream;

public record MockItem(
        NamespaceID namespace,
        int id,
        int stateId,
        Map<String, String> properties,
        Material material,
        int amount
) implements Item {

    @Override
    public Item withAmount(int amount) {
        return new MockItem(
                namespace,
                id, stateId,
                properties,
                material,
                amount
        );
    }

    @Override
    public @NotNull Stream<ItemComponent> components() {
        return Stream.empty();
    }

    @Override
    @SuppressWarnings("TypeParameterUnusedInFormals")
    public <C extends ItemComponent> @Nullable C getComponent(@NotNull String namespace) {
        return null;
    }
}
