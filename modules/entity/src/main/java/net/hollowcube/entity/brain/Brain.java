package net.hollowcube.entity.brain;

import net.hollowcube.entity.brain.navigator.Navigator;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Brain {

    @NotNull Entity entity();

    @NotNull Navigator navigator();

    boolean setPathTo(@NotNull Point point);

    default void setInstance(Instance instance) {}

    void tick(long time);



    // Temp stuff im not in love with

    default @Nullable Entity getTarget() {
        return null;
    }


}
