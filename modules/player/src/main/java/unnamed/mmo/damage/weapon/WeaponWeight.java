package unnamed.mmo.damage.weapon;

import org.jetbrains.annotations.Nullable;

public enum WeaponWeight {
    NONE,
    LIGHT,
    MEDIUM,
    HEAVY;

    public static @Nullable WeaponWeight getWeight(int ordinal) {
        WeaponWeight[] values = WeaponWeight.values();
        if (ordinal <= 0 || ordinal >= values.length) {
            return null;
        }
        return values[ordinal];
    }
}
