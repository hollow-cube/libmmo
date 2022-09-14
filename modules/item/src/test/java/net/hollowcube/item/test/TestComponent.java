package net.hollowcube.item.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.item.ItemComponent;

import static net.hollowcube.dfu.ExtraCodecs.string;

public record TestComponent(
        @NotNull String name
) implements ItemComponent {

    public static final Codec<TestComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            string("name", "unknown").forGetter(TestComponent::name)
    ).apply(i, TestComponent::new));

}
