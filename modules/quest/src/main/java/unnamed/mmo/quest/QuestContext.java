package unnamed.mmo.quest;

import com.mojang.serialization.Codec;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.storage.ObjectiveData;

public interface QuestContext {

    <T> @NotNull T get(Codec<T> codec);

    <T> void set(@NotNull Codec<T> codec, T value);

    @NotNull QuestContext child(@NotNull String name, @NotNull QuestObjective objective);

    @NotNull Player player();

    @NotNull Quest quest();


    @ApiStatus.Internal
    @NotNull ObjectiveData serialize();

}
