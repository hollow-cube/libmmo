package unnamed.mmo.quest.objective;

import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;
import unnamed.mmo.quest.storage.ObjectiveData;
import unnamed.mmo.quest.test.MockQuestContext;
import unnamed.mmo.quest.test.MockObjective;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TestSequenceObjective {

    @Test
    public void testSingleChild() {
        var obj1 = new MockObjective();
        var sequence = new SequenceObjective(List.of(obj1));

        var context = new MockQuestContext(null, null, new ObjectiveData(NamespaceID.from("test"), Map.of(), ""));
        var future = sequence.onStart(context);

        // Nothing has happened, no context should be saved
        var data = context.serialize();
        assertThat(data.data()).isEqualTo("");

        obj1.complete();
        data = context.serialize();
        assertThat(data.data()).isEqualTo("1");

        assertThat(future.isDone()).isTrue();
    }

    @Test
    public void testMultiObjective() {
        var obj1 = new MockObjective();
        var obj2 = new MockObjective();
        var sequence = new SequenceObjective(List.of(obj1, obj2));

        var context = new MockQuestContext(null, null, new ObjectiveData(NamespaceID.from("test"), Map.of(), ""));
        var future = sequence.onStart(context);

        // Nothing has happened, no context should be saved
        var data = context.serialize();
        assertThat(data.data()).isEqualTo("");

        obj1.complete();
        data = context.serialize();
        assertThat(data.data()).isEqualTo("1");

        obj2.complete();
        data = context.serialize();
        assertThat(data.data()).isEqualTo("2");

        assertThat(future.isDone()).isTrue();
    }

    @Test
    public void testSequenceResume() {
        var obj1 = new MockObjective();
        var obj2 = new MockObjective();
        var sequence = new SequenceObjective(List.of(obj1, obj2));

        // Context has data=1, so the sequence should resume from there.
        var context = new MockQuestContext(null, null,
                new ObjectiveData(NamespaceID.from("test"), Map.of(), "1"));
        var future = sequence.onStart(context);

        // Completing the second objective should result in the sequence being completed
        // since this is where we should have started.
        obj2.complete();

        assertThat(future.isDone()).isTrue();
    }

    @Test
    public void testSequenceStatus() {
        var obj1 = new MockObjective();
        var obj2 = new MockObjective();
        var sequence = new SequenceObjective(List.of(obj1, obj2));

        var context = new MockQuestContext(null, null, new ObjectiveData(NamespaceID.from("test"), Map.of(), ""));
        sequence.onStart(context);

        // Before anything we should get obj1 status
        assertThat(sequence.getCurrentStatus(context)).isSameInstanceAs(obj1.status());

        // After completing 1 we should see 2
        obj1.complete();
        assertThat(sequence.getCurrentStatus(context)).isSameInstanceAs(obj2.status());

        // After completing 2 we should continue getting 2
        obj2.complete();
        assertThat(sequence.getCurrentStatus(context)).isSameInstanceAs(obj2.status());
    }


}
