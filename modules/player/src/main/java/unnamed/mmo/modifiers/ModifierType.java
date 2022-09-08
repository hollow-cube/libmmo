package unnamed.mmo.modifiers;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModifierType {
    // A list of modifierID - base value
    private static final Map<String, Double> allModifiers = new HashMap<>();


    public static boolean doesModifierExist(@NotNull String modifierId) {
        return allModifiers.containsKey(modifierId);
    }

    public static double getBaseValue(@NotNull String modifierId) {
        return allModifiers.getOrDefault(modifierId, -1d);
    }

    public static final Codec<Map<String, Double>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.DOUBLE);
}
