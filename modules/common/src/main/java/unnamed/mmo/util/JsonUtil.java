package unnamed.mmo.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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


    //todo document that this merges by the type key.

    @Contract("_, _ -> new")
    public static @UnknownNullability JsonElement merge(@Nullable JsonElement left, @Nullable JsonElement right) {
        if (left == null) return right;
        if (right == null) return left;

        if (left.isJsonObject() && right.isJsonObject())
            return merge(left.getAsJsonObject(), right.getAsJsonObject());
        if (left.isJsonArray() && right.isJsonArray())
            return merge(left.getAsJsonArray(), right.getAsJsonArray());

        return right;
    }

    public static @NotNull JsonObject merge(@NotNull JsonObject left, @NotNull JsonObject right) {
        JsonObject merged = new JsonObject();
        for (var entry : left.entrySet()) {
            merged.add(entry.getKey(), entry.getValue());
        }
        for (var entry : right.entrySet()) {
            final String name = entry.getKey();
            // If left had the value, merge the two. Otherwise, just add the new entry.
            if (merged.has(entry.getKey())) {
                merged.add(name, merge(merged.get(name), entry.getValue()));
            } else {
                merged.add(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

    public static @NotNull JsonArray merge(@NotNull JsonArray left, @NotNull JsonArray right) {
        JsonArray merged = new JsonArray();

        Map<String, JsonElement> temp = new HashMap<>();
        for (JsonElement elem : left) {
            // If it isnt an object, we cannot merge. Move on
            if (!elem.isJsonObject()) {
                merged.add(elem);
                continue;
            }

            JsonObject obj = elem.getAsJsonObject();



        }

        //todo this is more challenging. It needs to merge by type field.
        return merged;
    }

}
