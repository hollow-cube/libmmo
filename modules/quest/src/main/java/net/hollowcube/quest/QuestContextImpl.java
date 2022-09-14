package net.hollowcube.quest;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.hollowcube.quest.event.QuestObjectiveChangeEvent;
import net.hollowcube.quest.objective.ObjectiveData;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.util.EventUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestContextImpl implements QuestContext {

    private final Player player;
    private final Quest quest;
    private final ObjectiveData data;

    protected final Map<String, QuestContextImpl> children = new HashMap<>();
    private Codec<Object> objCodec = null;
    private Object objData = null;

    public QuestContextImpl(@NotNull Player player, @NotNull Quest quest, @NotNull ObjectiveData data) {
        this.player = player;
        this.quest = quest;
        this.data = data;

        // Actively load all children
        for (var entry : data.children().entrySet()) {
            children.put(entry.getKey(), new QuestContextImpl(player, quest, entry.getValue()));
        }
    }

    @Override
    public @NotNull Player player() {
        return this.player;
    }

    @Override
    public @NotNull Quest quest() {
        return this.quest;
    }

    @Override
    public <T> @NotNull T get(Codec<T> codec) {
        if (objData == null) {
            objData = codec.decode(JsonOps.INSTANCE, JsonParser.parseString(data.data()))
                    //todo safety
                    .result().get().getFirst();
        }
        return (T) objData;
    }

    @Override
    public <T> void set(@NotNull Codec<T> codec, T value) {
        objCodec = (Codec<Object>) codec;
        objData = value;

        // Emit an objective changed event
        var event = new QuestObjectiveChangeEvent(player(), quest(), null); //todo do not have objective here
        EventUtil.safeDispatch(event);
    }

    @Override
    public @NotNull QuestContext child(@NotNull String name) {
        return children.computeIfAbsent(name, s -> new QuestContextImpl(player, quest, new ObjectiveData(Map.of(), "")));
    }


    @Override
    public @NotNull ObjectiveData serialize() {
        return new ObjectiveData(
                children.entrySet().stream()
                        .map(entry -> new Pair<>(entry.getKey(), entry.getValue().serialize()))
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
                //todo safety
                objCodec == null ?
                        // If the data has not changed we do not have a new codec, so just leave whatever was there.
                        data.data() :
                        objCodec.encodeStart(JsonOps.INSTANCE, objData).result().get().toString()
        );
    }
}
