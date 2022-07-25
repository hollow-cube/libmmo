package unnamed.mmo.item;

import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;

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
}
