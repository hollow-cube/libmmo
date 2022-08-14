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

    /**
     * Merge two {@link JsonElement}s recursively, combining arrays based on the "type" field.
     * <p>
     * The resulting json element is not necessarily a deep copy if the returned item has not been modified.
     */
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

        //todo lots of duplication, very yikes
        Map<String, JsonElement> temp = new HashMap<>();
        for (JsonElement elem : left) {
            // If it isnt an object, we cannot merge. Move on
            if (!elem.isJsonObject()) {
                merged.add(elem);
                continue;
            }
            JsonObject obj = elem.getAsJsonObject();
            // If it has a type field, we can merge. Otherwise, just add it.
            if (obj.has("type")) {
                temp.put(obj.get("type").getAsString(), elem);
            } else {
                merged.add(elem);
            }
        }

        for (JsonElement elem : right) {
            // If it isnt an object, we cannot merge. Move on
            if (!elem.isJsonObject()) {
                merged.add(elem);
                continue;
            }
            JsonObject obj = elem.getAsJsonObject();

            // If there is not a string type, we cannot merge. Move on
            JsonElement type = obj.get("type");
            if (!type.isJsonPrimitive()) {
                merged.add(elem);
                continue;
            }

            // If there is already an element of the same type, merge them.
            // Otherwise just add the element to temp map
            if (temp.containsKey(type.getAsString())) {
                temp.put(type.getAsString(), merge(temp.get(type.getAsString()), elem));
            } else {
                temp.put(type.getAsString(), elem);
            }
        }

        // Copy all entries from temp map into temp
        for (var entry : temp.entrySet()) {
            merged.add(entry.getValue());
        }

        return merged;
    }

}
