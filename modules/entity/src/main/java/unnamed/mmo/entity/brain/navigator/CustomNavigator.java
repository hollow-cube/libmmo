package unnamed.mmo.entity.brain.navigator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.pathfinding.PFNavigator;

final class CustomNavigator implements Navigator {
    private final PFNavigator navigator;

    public CustomNavigator(@NotNull Entity entity) {
        this.navigator = new PFNavigator(entity);
    }


    @Override
    public boolean setPathTo(@NotNull Point point) {
        return navigator.setPathTo(point, 0.5f);
    }

    @Override
    public boolean isActive() {
        return !navigator.isComplete();
    }

    @Override
    public void tick(long time) {
        navigator.tick(time);
    }
}
