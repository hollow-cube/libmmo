package net.hollowcube.entity.brain.navigator;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

final class HydrazineNavigator implements Navigator {
    private final net.minestom.server.entity.pathfinding.Navigator hydrazine;

    HydrazineNavigator(@NotNull Entity entity) {
        this.hydrazine = new net.minestom.server.entity.pathfinding.Navigator(entity);
    }

    @Override
    public void setInstance(@NotNull Instance instance) {
        hydrazine.setPathFinder(new HydrazinePathFinder(hydrazine.getPathingEntity(), instance.getInstanceSpace()));
    }

    @Override
    public boolean setPathTo(@NotNull Point point) {
        return hydrazine.setPathTo(point);
    }

    @Override
    public boolean isActive() {
        return hydrazine.getPathPosition() != null;
    }

    @Override
    public void tick(long time) {
        hydrazine.tick();
    }
}
