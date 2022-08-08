package unnamed.mmo.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Component;

import static unnamed.mmo.util.ExtraCodecs.string;

public record TestComponent(
        @NotNull String name
) implements Component {

    public static final Codec<TestComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            string("name", "unknown").forGetter(TestComponent::name)
    ).apply(i, TestComponent::new));

}
