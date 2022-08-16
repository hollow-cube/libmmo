package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.tag.Tag;
import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public record ListObjective(QuestObjective... objectives) implements QuestObjective {

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        int index = context.get(CODEC);

        return future;
    }

    private static final Codec<Integer> CODEC = Codec.INT.orElse(0);

//    private static class Data {
//
//        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
//                Codec.INT.fieldOf("index").forGetter(data -> data.index),
//                Codec.INT.fieldOf("indexA").forGetter(data -> data.indexA),
//                Codec.INT.fieldOf("indexB").forGetter(data -> data.indexB),
//                Codec.INT.fieldOf("indexC").forGetter(data -> data.indexC),
//                Codec.INT.fieldOf("indexD").forGetter(data -> data.indexD)
//        ).apply(i, Data::new));
//
//
//        int index = 0;
//        int indexA = 0;
//        int indexB = 0;
//        int indexC = 0;
//        int indexD = 0;
//
//        public Data(int index, int indexA, int indexB, int indexC, int indexD) {
//            this.index = index;
//            this.indexA = indexA;
//            this.indexB = indexB;
//            this.indexC = indexC;
//            this.indexD = indexD;
//        }
//    }
}
