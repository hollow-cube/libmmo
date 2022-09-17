package net.hollowcube.entity.brain.task;

import net.hollowcube.entity.brain.task.test.MockBrain;
import net.hollowcube.entity.brain.task.test.MockTask;
import net.hollowcube.entity.brain.task.test.TaskSubject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSequenceTask {
    @Test
    public void testEmptySequence() {
        var spec = new SequenceTask.Spec(List.of());
        var task = new SequenceTask(spec);
        var brain = new MockBrain();

        task.start(brain);

        //todo not really sure this should fail on empty, but the problem with passing is that it may be instantly
        //     started again, pass again, start again, etc. Which would not be good.
        assertEquals(Task.State.FAILED, task.getState());
    }

    @Test
    public void testSingleTaskSuccess() {
        var brain = new MockBrain();
        var mock1 = new MockTask(true);
        var spec = new SequenceTask.Spec(List.of(mock1.spec()));
        var task = new SequenceTask(spec);

        task.start(brain);
        task.tick(brain, System.currentTimeMillis());

        // Should have passed and mock1 should also have been run (passed)
        TaskSubject.assertThat(mock1).isComplete();
        TaskSubject.assertThat(task).isComplete();
    }

    @Test
    public void testMultiTaskSuccess() {
        var brain = new MockBrain();
        var mock1 = new MockTask(true);
        var mock2 = new MockTask(true);
        var spec = new SequenceTask.Spec(List.of(mock1.spec(), mock2.spec()));
        var task = new SequenceTask(spec);
        task.start(brain);

        task.tick(brain, System.currentTimeMillis());
        TaskSubject.assertThat(mock1).isComplete();

        task.tick(brain, System.currentTimeMillis());
        TaskSubject.assertThat(mock2).isComplete();

        TaskSubject.assertThat(task).isComplete();
    }
}
