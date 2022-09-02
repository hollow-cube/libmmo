package unnamed.mmo.damage.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import unnamed.mmo.item.ItemComponent;
import unnamed.mmo.dfu.ExtraCodecs;

import java.util.List;

public record Weapon(
        double attackSpeed,
        List<AppliedPotionEffect> randomEffects,
        WeaponWeight weight

) implements ItemComponent {

    private static final Codec<AppliedPotionEffect> APPLIED_POTION_EFFECTS = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("chance", 1d).forGetter(AppliedPotionEffect::chance),
            ExtraCodecs.POTION_EFFECT.fieldOf("effect").forGetter(AppliedPotionEffect::effect),
            Codec.INT.fieldOf("tickDuration").forGetter(AppliedPotionEffect::tickDuration),
            Codec.INT.fieldOf("amplifier").forGetter(AppliedPotionEffect::amplifier)
    ).apply(i, AppliedPotionEffect::new));

    private static final PrimitiveCodec<WeaponWeight> WEAPON_WEIGHT_CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<WeaponWeight> read(DynamicOps<T> ops, T input) {
            DataResult<Number> number = ops.getNumberValue(input);
            Number value = number.get().orThrow();
            WeaponWeight weaponWeight = WeaponWeight.getWeight(value.intValue());
            if(weaponWeight == null) {
                return DataResult.error("Number was outside valid bounds!");
            } else {
                return DataResult.success(weaponWeight);
            }
        }
        @Override
        public <T> T write(DynamicOps<T> ops, WeaponWeight value) {
            return ops.createNumeric(value.ordinal());
        }
    };
    public static final Codec<Weapon> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("attackSpeed").forGetter(Weapon::attackSpeed),
            APPLIED_POTION_EFFECTS.listOf().optionalFieldOf("randomEffects", List.of()).forGetter(Weapon::randomEffects),
            WEAPON_WEIGHT_CODEC.optionalFieldOf("weight", WeaponWeight.NONE).forGetter(Weapon::weight)
    ).apply(i, Weapon::new));

}
