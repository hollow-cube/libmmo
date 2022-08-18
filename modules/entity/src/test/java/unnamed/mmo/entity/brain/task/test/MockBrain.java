package unnamed.mmo.entity.brain.task.test;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.entity.brain.navigator.Navigator;

public class MockBrain implements Brain {

    @Override
    public @NotNull Entity entity() {
        return null;
    }

    @Override
    public @NotNull Navigator navigator() {
        return null;
    }

    @Override
    public boolean setPathTo(@NotNull Point point) {
        return false;
    }

    @Override
    public void tick(long time) {

    }
}
