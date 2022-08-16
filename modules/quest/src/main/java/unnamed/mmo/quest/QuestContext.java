package unnamed.mmo.quest;

import com.mojang.serialization.Codec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.tag.TagHandler;
import unnamed.mmo.quest.objective.QuestObjective;

import java.util.List;

public interface QuestContext {
    Player player();
    Quest quest();


    <T> T get(Codec<T> codec);
    <T> void set(Codec<T> codec, T value);
}
