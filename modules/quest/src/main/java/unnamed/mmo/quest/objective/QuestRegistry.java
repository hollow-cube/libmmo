package unnamed.mmo.quest.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class QuestRegistry {

    public record Quest(
            String questID,
            QuestObjective rootObjective
    ) {

        public static final Codec<Quest> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("name").forGetter(Quest::questID),
                QuestObjective.CODEC.fieldOf("objective").forGetter(Quest::rootObjective)
        ).apply(i, Quest::new));

    }
}
