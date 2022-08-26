package unnamed.mmo.quest.objective;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import unnamed.mmo.quest.test.MockObjective;
import unnamed.mmo.quest.test.MockQuestContext;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TestParallelObjective {

    @Test
    public void testSingleChild() {
        var obj1 = new MockObjective();
        var objective = new ParallelObjective(List.of(obj1));

        var context = new MockQuestContext(null, null, new ObjectiveData(Map.of(), ""));
        var future = objective.onStart(context);

        // Nothing has happened, no context should be saved
        var data = context.serialize();
        assertThat(data.data()).isEqualTo("");

        // Should result in a bitmask of 1, since the first quest is completed
        obj1.complete();
        data = context.serialize();
        assertThat(data.data()).isEqualTo("1");

        // Objective should also be complete
        assertThat(future.isDone()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testMultiChild(boolean sequential) {
        var obj1 = new MockObjective();
        var obj2 = new MockObjective();
        var objective = new ParallelObjective(List.of(obj1, obj2));

        var context = new MockQuestContext(null, null, new ObjectiveData(Map.of(), ""));
        var future = objective.onStart(context);

        if (sequential) {
            obj1.complete();
            var data = context.serialize();
            assertThat(data.data()).isEqualTo("1");

            obj2.complete();
        } else {
            obj2.complete();
            var data = context.serialize();
            assertThat(data.data()).isEqualTo("2");

            obj1.complete();
        }

        var data = context.serialize();
        assertThat(data.data()).isEqualTo("3");

        // Objective should also be complete
        assertThat(future.isDone()).isTrue();
    }

    @Test
    public void testSequenceResume() {
        var obj1 = new MockObjective();
        var obj2 = new MockObjective();
        var objective = new ParallelObjective(List.of(obj1, obj2));

        // Start with second quest completed already
        var context = new MockQuestContext(null, null, new ObjectiveData(Map.of(), "2"));
        var future = objective.onStart(context);

        // Completing the first one should result in the parallel objective being completed
        obj1.complete();

        assertThat(future.isDone()).isTrue();
    }

}
