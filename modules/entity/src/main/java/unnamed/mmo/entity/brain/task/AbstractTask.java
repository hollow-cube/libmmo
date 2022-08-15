package unnamed.mmo.entity.brain.task;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;

public abstract non-sealed class AbstractTask implements Task {
    private State state = State.INIT;

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void start(@NotNull Brain brain) {
        this.state = State.RUNNING;
    }

    protected void end(boolean success) {
        this.state = success ? State.COMPLETE : State.FAILED;
    }

}
