package unnamed.mmo.blocks.ore.quest;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.event.EventListener;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.ore.event.PlayerOreBreakEvent;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.util.ExtraCodecs;

import java.util.concurrent.CompletableFuture;

public record MineOreObjective(
        @NotNull NamespaceID ore,
        int count
) implements QuestObjective {
    public static final Codec<MineOreObjective> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("ore").forGetter(MineOreObjective::ore),
            Codec.INT.fieldOf("count").forGetter(MineOreObjective::count)
    ).apply(i, MineOreObjective::new));

    private static final Codec<Integer> CURRENT = Codec.INT.orElse(0);

    @Override
    public CompletableFuture<Void> onStart(QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

        context.player().eventNode().addListener(EventListener.builder(PlayerOreBreakEvent.class)
                .expireWhen(unused -> complete.isDone())
                .handler(event -> {
                    // Ensure we are mining the right ore
                    if (!event.getOre().namespace().equals(ore())) {
                        System.out.println("WRONG ORE");
                        return;
                    }

                    System.out.println("MINE ORE");
                    int current = context.get(CURRENT) + 1;
                    context.set(CURRENT, current);

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
            super(NamespaceID.from("unnamed:mine_ore"), MineOreObjective.class, MineOreObjective.CODEC);
        }
    }
}
