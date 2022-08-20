package unnamed.mmo.quest.objective;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.utils.NamespaceID;
import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;

public record BlockBreakObjective(int blockId, int count) implements QuestObjective {

    private static final Codec<BlockBreakObjective> codec = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("id").forGetter(BlockBreakObjective::blockId),
            Codec.INT.fieldOf("count").forGetter(BlockBreakObjective::count)
            ).apply(i, BlockBreakObjective::new)
    );

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

//        context.player().eventNode().addListener(EventListener.builder(PlayerBlockBreakEvent.class)
//                .expireWhen(event -> complete.isDone())
//                .filter(event -> event.getBlock().id() == blockId)
//                .handler(event -> {
//                    int current = context.get(COUNT);
//                    current++;
//                    if (current == count()) {
//                        complete.complete(null);
//                        return;
//                    }
//                    context.set(COUNT, current);
//                })
//                .build());

        return complete;
    }
    @AutoService(QuestObjective.Factory.class)
    static class Factory extends QuestObjective.Factory {
        public Factory() {
            super(NamespaceID.from("unnammedmmo", "block-break-objective"), BlockBreakObjective.class, codec);
        }
    }
}
