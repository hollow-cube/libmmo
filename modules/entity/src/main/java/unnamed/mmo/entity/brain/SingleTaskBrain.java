package unnamed.mmo.entity.brain;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.navigator.Navigator;
import unnamed.mmo.entity.brain.task.Task;

public class SingleTaskBrain implements Brain {

    private final Entity entity;
    private final Navigator navigator;
    private final Task task;

    public SingleTaskBrain(@NotNull Entity entity, @NotNull Task task) {
        this.entity = entity;
        this.navigator = Navigator.enodia(entity);
        this.task = task;
    }


    @Override
    public @NotNull Entity entity() {
        return entity;
    }

    @Override
    public @NotNull Navigator navigator() {
        return navigator;
    }

    @Override
    public boolean setPathTo(@NotNull Point point) {
        return navigator.setPathTo(point);
    }

    private boolean failed = false;
    private Instance lastInstance = null;

    @Override
    public void setInstance(Instance instance) {
        navigator.setInstance(instance);
        lastInstance = instance;
    }

    @Override
    public void tick(long time) {
        if (lastInstance == null) return;

        navigator.tick(time);
        switch (task.getState()) {
            case INIT, COMPLETE -> task.start(this);
            case RUNNING -> task.tick(this);
            case FAILED -> {
                if (!failed)
                    System.out.println("Failed");
                failed = true;
            }
        }
    }
}
