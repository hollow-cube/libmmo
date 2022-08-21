package unnamed.mmo.entity.brain.navigator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

public sealed interface Navigator permits CustomNavigator, EnodiaNavigator, HydrazineNavigator {

    static @NotNull Navigator enodia(@NotNull Entity entity) {
        return new EnodiaNavigator(entity);
    }

    static @NotNull Navigator hydrazine(@NotNull Entity entity) {
        return new HydrazineNavigator(entity);
    }

    static @NotNull Navigator custom(@NotNull Entity entity) {
        return new CustomNavigator(entity);
    }

    default void setInstance(@NotNull Instance instance) {}

    boolean setPathTo(@NotNull Point point);

    boolean isActive();

    void tick(long time);

}
