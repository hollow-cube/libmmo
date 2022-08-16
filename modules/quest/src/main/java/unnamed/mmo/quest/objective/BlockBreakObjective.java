package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;

public record BlockBreakObjective(int blockId, int count) implements QuestObjective {

    private static final Codec<Integer> COUNT = Codec.INT.orElse(0);

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

        context.player().eventNode().addListener(EventListener.builder(PlayerBlockBreakEvent.class)
                .expireWhen(event -> complete.isDone())
                .filter(event -> event.getBlock().id() == blockId)
                .handler(event -> {
                    int current = context.get(COUNT);
                    current++;
                    if (current == count()) {
                        complete.complete(null);
                        return;
                    }
                    context.set(COUNT, current);
                })
                .build());

        return complete;
    }
}
