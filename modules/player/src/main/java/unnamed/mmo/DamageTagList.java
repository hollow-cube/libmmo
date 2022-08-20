package unnamed.mmo;

import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

public class DamageTagList {

    public static final Tag<Double> ENTITY_WEIGHT_TAG = Tag.Double("mob-weight");
    public static final Tag<Integer> WEAPON_TYPE_TAG = Tag.Integer("weapon-weight");

    public static WeaponWeight getWeaponWeight(ItemStack itemStack) {
        if(itemStack.hasTag(WEAPON_TYPE_TAG)) {
            int number = itemStack.getTag(WEAPON_TYPE_TAG);
            WeaponWeight[] list = WeaponWeight.values();
            if(number >= 1 && number < list.length) {
                return list[number];
            } else {
                return WeaponWeight.NONE;
            }
        } else {
            return WeaponWeight.NONE;
        }
    }

    public enum WeaponWeight {
        NONE,
        LIGHT,
        MEDIUM,
        HEAVY
    }
}
