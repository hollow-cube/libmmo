package unnamed.mmo.quest;

import com.mojang.serialization.Codec;
import net.minestom.server.entity.Player;

public interface QuestContext {
    Player player();
    Quest quest();


    <T> T get(Codec<T> codec);
    <T> void set(Codec<T> codec, T value);
}
