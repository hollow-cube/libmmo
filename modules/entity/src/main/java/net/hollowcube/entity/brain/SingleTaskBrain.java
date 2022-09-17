package net.hollowcube.entity.brain;

import net.hollowcube.entity.brain.navigator.Navigator;
import net.hollowcube.entity.brain.task.Task;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleTaskBrain implements Brain {

    private final Entity entity;
    private final Navigator navigator;
    private final Task task;

    private Entity target = null;

    public SingleTaskBrain(@NotNull Entity entity, @NotNull Task task) {
        this.entity = entity;
        this.navigator = Navigator.motion(entity);
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

        //todo also player death or anything else?
        if (target != null && target.isRemoved()) {
            target = null;
        }

        navigator.tick(time);
        switch (task.getState()) {
            case INIT, COMPLETE -> task.start(this);
            case RUNNING -> task.tick(this, time);
            case FAILED -> {
                if (!failed)
                    System.out.println("Failed");
                failed = true;
            }
        }
    }

    @Override
    public @Nullable Entity getTarget() {
        return target;
    }

    public void setTarget(@NotNull Entity target) {
        this.target = target;
    }
}
