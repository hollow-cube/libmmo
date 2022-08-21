package unnamed.mmo.entity.brain;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.navigator.Navigator;

public interface Brain {

    @NotNull Entity entity();

    @NotNull Navigator navigator();

    boolean setPathTo(@NotNull Point point);

    default void setInstance(Instance instance) {}

    void tick(long time);


}