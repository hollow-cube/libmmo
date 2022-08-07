package unnamed.mmo.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.jetbrains.annotations.NotNull;

public interface DelegateCodec<T> extends Codec<T> {

    @NotNull Codec<T> codec();


    @Override
    default <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return codec().decode(ops, input);
    }

    @Override
    default <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return codec().encode(input, ops, prefix);
    }
}
