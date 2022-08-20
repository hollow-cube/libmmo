package unnamed.mmo.quest;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.storage.ObjectiveData;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestContextImpl implements QuestContext {

    private final Player player;
    private final ObjectiveData data;

    private final Map<String, QuestContextImpl> children = new HashMap<>();
    private Codec<Object> objCodec = null;
    private Object objData = null;

    public QuestContextImpl(@NotNull Player player, @NotNull ObjectiveData data) {
        this.player = player;
        this.data = data;

        // Actively load all children
        for (var entry : data.children().entrySet()) {
            children.put(entry.getKey(), new QuestContextImpl(player, entry.getValue()));
        }
    }

    @Override
    public @NotNull Player player() {
        return this.player;
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
    }

    @Override
    public @NotNull QuestContext child(@NotNull String name, @NotNull QuestObjective objective) {
        NamespaceID type = QuestObjective.Factory.TYPE_REGISTRY.get(objective.getClass()).namespace();
        return children.computeIfAbsent(name, s -> new QuestContextImpl(player, new ObjectiveData(type, Map.of(), "")));
    }


    @Override
    public @NotNull ObjectiveData serialize() {
        return new ObjectiveData(
                data.type(),
                children.entrySet().stream()
                        .map(entry -> new Pair<>(entry.getKey(), entry.getValue().serialize()))
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
                //todo safety
                objCodec.encodeStart(JsonOps.INSTANCE, objData).result().get().toString()
        );
    }
}
