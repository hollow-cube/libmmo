package unnamed.mmo.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.dfu.EnvVarOps;

import java.util.Locale;

public final class ConfigProvider {
    private static final EnvVarOps ops = new EnvVarOps();

    public static <T> @NotNull T load(@NotNull String prefix, @NotNull Codec<T> codec) {


        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(prefix.toUpperCase(Locale.ROOT)))
                .result()
                .orElse(null);
        Check.notNull(result, "Config unable to load");
        return result.getFirst();
    }
}
