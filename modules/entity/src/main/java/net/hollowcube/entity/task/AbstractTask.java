package net.hollowcube.entity.task;

import net.hollowcube.entity.SmartEntity;
import org.jetbrains.annotations.NotNull;

public abstract non-sealed class AbstractTask implements Task {
    private State state = State.INIT;

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void start(@NotNull SmartEntity entity) {
        this.state = State.RUNNING;
    }

    protected void end(boolean success) {
        this.state = success ? State.COMPLETE : State.FAILED;
    }

}
