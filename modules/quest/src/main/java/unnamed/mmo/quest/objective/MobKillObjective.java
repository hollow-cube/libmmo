package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.EventListener;
import unnamed.mmo.damage.EntityKilledByEntityEvent;
import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;

public record MobKillObjective(EntityType type, int count) implements QuestObjective {

    private static final Codec<Integer> COUNT = Codec.INT.orElse(0);

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

//        context.player().eventNode().addListener(EventListener.builder(EntityKilledByEntityEvent.class)
//                .expireWhen(event -> complete.isDone())
//                .filter(event -> event.getEntity().getEntityType() == type &&
//                event.getEntity().getUuid() == context.player().getUuid())
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
}
