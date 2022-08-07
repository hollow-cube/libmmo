package unnamed.mmo.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExtraCodecs {
    private ExtraCodecs() {}

    public static final Codec<NamespaceID> NAMESPACE_ID = Codec.STRING.xmap(NamespaceID::from, NamespaceID::asString);

    public static final Codec<Material> MATERIAL = Codec.STRING.xmap(Material::fromNamespaceId, Material::toString);

    public static @NotNull MapCodec<String> string(@NotNull String name, @Nullable String defaultValue) {
        return Codec.STRING.optionalFieldOf(name, defaultValue);
    }


}
