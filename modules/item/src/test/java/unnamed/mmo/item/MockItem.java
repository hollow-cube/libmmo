package unnamed.mmo.item;

import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.item.component.ItemComponent;

import java.util.Map;

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
    public <C extends ItemComponent> @Nullable C getComponent(@NotNull String namespace) {
        return null;
    }
}
