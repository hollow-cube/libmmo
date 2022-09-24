package net.hollowcube.entity.brain.task.test;

import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;
import net.hollowcube.entity.task.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskSubject extends Subject {
    private final Task actual;

    public static @NotNull TaskSubject assertThat(@Nullable Task actual) {
        return Truth.assertAbout(tasks()).that(actual);
    }

    protected TaskSubject(FailureMetadata metadata, @Nullable Task actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void isComplete() {
        if (actual.getState() != Task.State.COMPLETE) {
            failWithActual(Fact.simpleFact("Expected task to be complete, but it was " + actual.getState()));
        }
    }


    private static final Factory<TaskSubject, Task> tasks() {
        return TaskSubject::new;
    }
}
