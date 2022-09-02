package unnamed.mmo.dfu;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DFUUtil {

    public static <T, S> Map<T, S> pairListToMap(List<Pair<T, S>> pairList) {
        return pairList.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public static <T, S> List<Pair<T, S>> mapToPairList(Map<T, S> map) {
        return map.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList();
    }

    public static <T> @NotNull T value(@NotNull Either<? extends T, ? extends T> either) {
        return either.map(Function.identity(), Function.identity());
    }
}
