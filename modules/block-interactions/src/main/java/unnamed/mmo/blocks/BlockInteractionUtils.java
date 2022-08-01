package unnamed.mmo.blocks;

import net.minestom.server.utils.NamespaceID;

public class BlockInteractionUtils {

    public static NamespaceID createInteractionID(String name) {
        // So that it's easy to change the NamespacedID domain
        return NamespaceID.from("unnammedmmo", name);
    }
}
