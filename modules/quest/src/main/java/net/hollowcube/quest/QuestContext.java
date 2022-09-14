package net.hollowcube.quest;

import com.mojang.serialization.Codec;
import net.hollowcube.quest.objective.ObjectiveData;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface QuestContext {

    <T> @NotNull T get(Codec<T> codec);

    <T> void set(@NotNull Codec<T> codec, T value);

    @NotNull QuestContext child(@NotNull String name);

    @NotNull Player player();

    @NotNull Quest quest();


    @ApiStatus.Internal
    @NotNull ObjectiveData serialize();

}
