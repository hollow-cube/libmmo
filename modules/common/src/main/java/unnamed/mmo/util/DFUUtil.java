package unnamed.mmo.util;

import com.mojang.datafixers.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DFUUtil {

    public static <T, S> Map<T, S> pairListToMap(List<Pair<T, S>> pairList) {
        return pairList.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public static <T, S> List<Pair<T, S>> mapToPairList(Map<T, S> map) {
        return map.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList();
    }
}
