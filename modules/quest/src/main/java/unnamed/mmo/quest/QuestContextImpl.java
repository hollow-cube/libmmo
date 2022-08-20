package unnamed.mmo.quest;

import com.google.gson.JsonParser;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.quest.objective.QuestObjective;
import unnamed.mmo.quest.objective.QuestRegistry;
import unnamed.mmo.util.ExtraCodecs;

import java.util.HashMap;
import java.util.Map;

import static unnamed.mmo.util.ExtraCodecs.lazy;


public class QuestContextImpl implements QuestContext {
    public static final Codec<QuestContextImpl> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("type").forGetter(QuestContextImpl::type),
            Codec.PASSTHROUGH.fieldOf("data").forGetter(QuestContextImpl::data),
            Codec.unboundedMap(Codec.STRING, lazy(() -> QuestContextImpl.CODEC)).fieldOf("children").forGetter(QuestContextImpl::children)
    ).apply(i, QuestContextImpl::new));

    private final NamespaceID type;
    private final Map<String, QuestContextImpl> children;
    private Dynamic<?> data;

    private Object dataCache = null;
    // The presence of this object indicates that `data` must be overridden.
    private Codec<Object> dataCodec = null;

    public QuestContextImpl(@NotNull NamespaceID type, @NotNull Dynamic<?> data, @NotNull Map<String, QuestContextImpl> children) {
        this.type = type;
        this.data = data;
        this.children = new HashMap<>();
        this.children.putAll(children);
    }

    public @NotNull NamespaceID type() {
        return type;
    }

    public @NotNull Dynamic<?> data() {
        if (dataCodec != null) {
            //todo this is pretty cursed
            var a = dataCodec.encodeStart(JsonOps.INSTANCE, dataCache);
            data = new Dynamic<>(JsonOps.INSTANCE, a.result().get());
            dataCodec = null;
        }
        return data;
    }

    public @NotNull Map<String, QuestContextImpl> children() {
        return children;
    }


    @Override
    public <T> @NotNull T get(Codec<T> codec) {
        if (dataCache == null)
            dataCache = codec.decode(data);
        return (T) dataCache;
    }

    @Override
    public <T> void set(@NotNull Codec<T> codec, T value) {
        dataCodec = (Codec<Object>) codec;
        dataCache = value;
    }

    @Override
    public @NotNull QuestContext child(@NotNull String name, @NotNull QuestObjective objective) {
        NamespaceID type = QuestObjective.Factory.TYPE_REGISTRY.get(objective.getClass()).namespace();
//        QuestContextImpl child = children.computeIfAbsent(name, s -> new QuestContextImpl(type, ))
        return null;
    }

    public static void main(String[] args) {
        var json = JsonParser.parseString("""
                1
                """);
        var dynamic = new Dynamic<>(JsonOps.INSTANCE, json);
        var result = Codec.STRING.orElse("a").decode(dynamic).result().get();
        System.out.println(result);
    }
}
