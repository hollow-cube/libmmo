package unnamed.mmo.entity.brain.task;

import org.junit.jupiter.api.Test;
import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.entity.brain.task.test.MockBrain;

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
            task.tick(brain);
        }
        assertThat(task.getState()).isEqualTo(Task.State.COMPLETE);
    }

}
