package unnamed.mmo.entity.brain.navigator;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

public sealed interface Navigator permits EnodiaNavigator {

    static @NotNull Navigator enodia(@NotNull Brain brain) {
        return new EnodiaNavigator(brain);
    }

    boolean setPathTo(@NotNull Point point);

}
