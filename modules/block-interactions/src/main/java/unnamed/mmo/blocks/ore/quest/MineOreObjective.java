package unnamed.mmo.blocks.ore.quest;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.EventListener;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.ore.Ore;
import unnamed.mmo.blocks.ore.event.PlayerOreBreakEvent;
import unnamed.mmo.quest.QuestContext;
import unnamed.mmo.quest.objective.Objective;
import unnamed.mmo.util.ExtraCodecs;

import java.util.concurrent.CompletableFuture;

public record MineOreObjective(
        @NotNull NamespaceID ore,
        int count
) implements Objective {
    public static final Codec<MineOreObjective> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("ore").forGetter(MineOreObjective::ore),
            Codec.INT.fieldOf("count").forGetter(MineOreObjective::count)
    ).apply(i, MineOreObjective::new));

    private static final Codec<Integer> CURRENT = Codec.INT.orElse(0);

    @Override
    public @NotNull CompletableFuture<Void> onStart(@NotNull QuestContext context) {
        CompletableFuture<Void> complete = new CompletableFuture<>();

        context.player().eventNode().addListener(EventListener.builder(PlayerOreBreakEvent.class)
                .expireWhen(unused -> complete.isDone())
                .handler(event -> {
                    // Ensure we are mining the right ore
                    if (!event.getOre().namespace().equals(ore())) {
                        return;
                    }

                    int current = context.get(CURRENT) + 1;
                    context.set(CURRENT, current);

                    if (current == count()) {
                        complete.complete(null);
                    }
                })
                .build());

        return complete;
    }

    @Override
    public @NotNull Component getCurrentStatus(@NotNull QuestContext context) {
        final int current = context.get(CURRENT);
        final Ore ore = Ore.fromNamespaceId(ore());
        return Component.translatable("objective.mine_ore.status",
                Component.translatable(ore.translationKey()),   // Ore name
                Component.text(current),                        // Current count
                Component.text(count()));                       // Total count
    }


    @AutoService(Objective.Factory.class)
    public static class Factory extends Objective.Factory {
        public Factory() {
            super(NamespaceID.from("unnamed:mine_ore"), MineOreObjective.class, MineOreObjective.CODEC);
        }
    }
}
