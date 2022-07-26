package net.hollowcube.quest.test;

import com.mojang.serialization.Codec;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.quest.QuestContext;
import net.hollowcube.quest.objective.Objective;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MockObjective implements Objective {

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private final Component status = Component.text(ThreadLocalRandom.current().nextInt());

    private static final Codec<Integer> CURRENT = Codec.INT.orElse(0);

    @Override
    public @NotNull CompletableFuture<Void> onStart(@NotNull QuestContext context) {
        context.set(CURRENT, context.get(CURRENT) + 1);
        return future;
    }

    @Override
    public @NotNull Component getCurrentStatus(@NotNull QuestContext context) {
        return status;
    }

    public void complete() {
        future.complete(null);
    }

    public Component status() {
        return status;
    }

}
