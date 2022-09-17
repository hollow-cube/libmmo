package net.hollowcube.entity.brain.task.test;

import net.hollowcube.entity.brain.task.AbstractTask;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.entity.brain.Brain;

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
