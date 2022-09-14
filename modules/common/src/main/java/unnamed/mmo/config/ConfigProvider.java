package unnamed.mmo.config;

import com.mojang.serialization.Codec;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.dfu.EnvVarOps;

import java.util.Locale;

public final class ConfigProvider {

    public static <T> @NotNull T load(@NotNull String prefix, @NotNull Codec<T> codec) {
        var result = EnvVarOps.DOTENV.withDecoder(codec)
                .apply(prefix.toUpperCase(Locale.ROOT))
                .result()
                .orElse(null);
        Check.notNull(result, "Config unable to load");
        return result.getFirst();
    }
}
