package net.hollowcube.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.dfu.ExtraCodecs;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.Resource;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public record ModifierType(NamespaceID namespace, double defaultValue) implements Resource {

    public static final Codec<ModifierType> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(ModifierType::namespace),
            Codec.DOUBLE.fieldOf("defaultValue").forGetter(ModifierType::defaultValue)
    ).apply(i, ModifierType::new));

    public static final Registry<ModifierType> REGISTRY = Registry.codec("modifiers", CODEC);


    public static boolean doesModifierExist(@NotNull String modifierId) {
        if(!modifierId.startsWith("starlight:")) {
            return REGISTRY.get("starlight:" + modifierId) != null;
        } else {
            return REGISTRY.get(modifierId) != null;
        }
    }

    public static double getBaseValue(@NotNull String modifierId) {
        if(!modifierId.startsWith("starlight:")) {
            return REGISTRY.required("starlight:" + modifierId).defaultValue;
        } else {
            return REGISTRY.required(modifierId).defaultValue;
        }
    }
}