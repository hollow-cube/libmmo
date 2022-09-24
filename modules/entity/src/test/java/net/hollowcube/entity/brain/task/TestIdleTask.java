package net.hollowcube.entity.brain.task;

import net.hollowcube.data.number.NumberProvider;
import net.hollowcube.entity.brain.task.test.MockBrain;
import net.hollowcube.entity.task.IdleTask;
import net.hollowcube.entity.task.Task;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestIdleTask {

    @Test
    public void testHappyCase() {
        var spec = new IdleTask.Spec(NumberProvider.constant(5));
        var task = new IdleTask(spec);
        // Entity should do nothing, so we can happily use any old entity.
        var brain = new MockBrain();

        assertThat(task.getState()).isEqualTo(Task.State.INIT);
        task.start(brain);
        for (int i = 0; i < 5; i++) {
            assertThat(task.getState()).isEqualTo(Task.State.RUNNING);
            task.tick(brain, 0);
        }
        assertThat(task.getState()).isEqualTo(Task.State.COMPLETE);
    }

}
