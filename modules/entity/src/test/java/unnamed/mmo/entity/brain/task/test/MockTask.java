package unnamed.mmo.entity.brain.task.test;

import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.entity.brain.task.AbstractTask;
import unnamed.mmo.entity.brain.task.Task;

public class MockTask extends AbstractTask {
    private final Boolean pass;

    public MockTask(Boolean pass) {
        this.pass = pass;
    }

    @Override
    public void tick(@NotNull Brain brain, long time) {
        if (pass != null)
            end(pass);
    }

    public @NotNull Spec spec() {
        return () -> MockTask.this;
    }




}
