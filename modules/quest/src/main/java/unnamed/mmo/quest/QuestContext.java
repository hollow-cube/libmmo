package unnamed.mmo.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.objective.QuestRegistry;

public interface QuestContext {

    <T> @NotNull T get(Codec<T> codec);

    <T> void set(@NotNull Codec<T> codec, T value);

    @NotNull QuestContext child(@NotNull String name, @NotNull QuestObjective objective);

}
