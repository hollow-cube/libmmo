package unnamed.mmo.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JsonUtil {

    @Contract("_ -> new")
    public static @NotNull JsonObject merge(@NotNull JsonObject... objects) {
        //todo this will not be functional right now, need to do a deep merge.
        // for example objects in the components array within states must be merged correctly.
        JsonObject out = new JsonObject();
        for (JsonObject obj : objects) {
            for (var entry : obj.entrySet()) {
                out.add(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    public static <T> @NotNull List<T> asList(@NotNull JsonArray array, Function<JsonElement, T> mapper) {
        List<T> items = new ArrayList<>();
        for (JsonElement elem : array)
            items.add(mapper.apply(elem));
        return items;
    }

}
