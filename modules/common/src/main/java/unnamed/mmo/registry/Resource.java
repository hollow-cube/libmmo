package unnamed.mmo.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Resource extends Keyed {

    @Contract(pure = true)
    @NotNull NamespaceID namespace();

    @Override
    default @NotNull Key key() {
        return namespace();
    }

    @Contract(pure = true)
    default @NotNull String name() {
        return namespace().asString();
    }


    interface Id extends Resource {

        @Contract(pure = true)
        int id();

    }


    record Type(@NotNull String name) {

    }
}
