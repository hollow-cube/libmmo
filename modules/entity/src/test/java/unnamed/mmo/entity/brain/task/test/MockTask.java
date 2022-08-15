package unnamed.mmo.entity.brain.task.test;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.entity.brain.task.AbstractTask;
import unnamed.mmo.entity.brain.task.Task;

public class MockTask extends AbstractTask {
    private final boolean pass;

    public MockTask(boolean pass) {
        this.pass = pass;
    }

    @Override
    public void tick(@NotNull Brain brain) {
        end(pass);
    }

    @Override
    public @NotNull Task deepCopy() {
        return new MockTask(pass);
    }
}
