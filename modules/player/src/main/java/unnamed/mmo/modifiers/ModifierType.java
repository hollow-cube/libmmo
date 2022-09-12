package unnamed.mmo.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.registry.Resource;

public record ModifierType(String id, double defaultValue) implements Resource {

   public static final Codec<ModifierType> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("modifier").forGetter(ModifierType::id),
            Codec.DOUBLE.fieldOf("defaultValue").forGetter(ModifierType::defaultValue)
    ).apply(i, ModifierType::new));

    public static final Registry<ModifierType> REGISTRY = Registry.codec("modifiers", CODEC);


    public static boolean doesModifierExist(@NotNull String modifierId) {
        return REGISTRY.get("starlight:" + modifierId) != null;
    }

    public static double getBaseValue(@NotNull String modifierId) {
        return REGISTRY.required("starlight:" + modifierId).defaultValue;
    }


    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("starlight", id);
    }
}
