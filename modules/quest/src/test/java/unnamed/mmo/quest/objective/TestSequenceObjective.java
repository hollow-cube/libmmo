package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import unnamed.mmo.quest.Quest;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.QuestContextImpl;
import unnamed.mmo.quest.storage.ObjectiveData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.common.truth.Truth.assertThat;

public class TestSequenceObjective {

    @Test
    public void testSequenceObjective1() {
        var obj1 = new TestQuestObjective();
        var obj2 = new TestQuestObjective();
        var sequence = new SequenceObjective(List.of(obj1, obj2));

        var context = new TestQuestContext(null, null, new ObjectiveData(NamespaceID.from("test"), Map.of(), ""));
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


    private static class TestQuestObjective implements QuestObjective {

        private CompletableFuture<Void> future = new CompletableFuture<>();

        private static final Codec<Integer> CURRENT = Codec.INT.orElse(0);

        @Override
        public CompletableFuture<Void> onStart(QuestContext context) {
            context.set(CURRENT, context.get(CURRENT) + 1);
            return future;
        }

        public void complete() {
            future.complete(null);
        }

    }

    private static class TestQuestContext extends QuestContextImpl {

        public TestQuestContext(@NotNull Player player, @NotNull Quest quest, @NotNull ObjectiveData data) {
            super(player, quest, data);
        }

        @Override
        public @NotNull QuestContext child(@NotNull String name, @NotNull QuestObjective objective) {
            NamespaceID type = NamespaceID.from("test");
            return children.computeIfAbsent(name, s -> new QuestContextImpl(player(), quest(), new ObjectiveData(type, Map.of(), "")));
        }
    }
}
