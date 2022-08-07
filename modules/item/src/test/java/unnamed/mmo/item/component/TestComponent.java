package unnamed.mmo.item.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.registry.Registry;
import org.jetbrains.annotations.NotNull;

import static unnamed.mmo.util.ExtraCodecs.string;

public record TestComponent(
        @NotNull String name
) implements ItemComponent {

    public static final Codec<TestComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            string("name", "unknown").forGetter(TestComponent::name)
    ).apply(i, TestComponent::new));

    public TestComponent(Registry.Properties obj) {
        //todo abstraction on data loading
        this(obj.getString("name"));
    }
}
