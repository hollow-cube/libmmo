package net.hollowcube.entity.navigator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.entity.motion.MotionNavigator;
import net.hollowcube.entity.motion.MotionNavigatorSlime;

public interface Navigator {

    static @NotNull Navigator hydrazine(@NotNull Entity entity) {
        return new HydrazineNavigator(entity);
    }

    static @NotNull Navigator custom(@NotNull Entity entity) {
        return new CustomNavigator(entity);
    }

    static @NotNull Navigator motion(@NotNull Entity entity) {
        return new MotionNavigator(entity);
    }

    static @NotNull Navigator motionSlime(@NotNull Entity entity) {
        return new MotionNavigatorSlime(entity);
    }

    default void setInstance(@NotNull Instance instance) {}

    boolean setPathTo(@Nullable Point point);

    boolean isActive();

    void tick(long time);

}
