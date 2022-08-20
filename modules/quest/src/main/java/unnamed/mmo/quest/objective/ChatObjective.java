package unnamed.mmo.quest.objective;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.utils.NamespaceID;
import unnamed.mmo.quest.QuestContext;

import java.util.concurrent.CompletableFuture;

/**
 * A test objective requiring the player to send 5 chat messages
 */
public record ChatObjective(int count) implements QuestObjective {

    private static final Codec<ChatObjective> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("value").forGetter(ChatObjective::count)
    ).apply(i, ChatObjective::new));

    private static final Codec<Integer> MESSAGES = Codec.INT.orElse(0);

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

        context.player().eventNode().addListener(EventListener.builder(PlayerChatEvent.class)
                .expireWhen(unused -> complete.isDone())
                .handler(event -> {
                    System.out.println("CHAT MESSAGE");
                    int current = context.get(MESSAGES) + 1;
                    context.set(MESSAGES, current);

                    if (current == count()) {
                        complete.complete(null);
                    }
                })
                .build());

        return complete;
    }

    @AutoService(QuestObjective.Factory.class)
    public static class Factory extends QuestObjective.Factory {
        public Factory() {
            super(
                    NamespaceID.from("unnamed", "chat_count"),
                    ChatObjective.class,
                    ChatObjective.CODEC
            );
        }
    }
}
