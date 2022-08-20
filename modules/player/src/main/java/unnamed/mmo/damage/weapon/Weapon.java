package unnamed.mmo.damage.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import unnamed.mmo.item.ItemComponent;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;

public record Weapon(
        double attackSpeed,
        List<AppliedPotionEffect> randomEffects

) implements ItemComponent {

    private static final Codec<AppliedPotionEffect> APPLIED_POTION_EFFECTS = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("chance", 1d).forGetter(AppliedPotionEffect::chance),
            ExtraCodecs.POTION_EFFECT.fieldOf("effect").forGetter(AppliedPotionEffect::effect),
            Codec.INT.fieldOf("tickDuration").forGetter(AppliedPotionEffect::tickDuration),
            Codec.INT.fieldOf("amplifier").forGetter(AppliedPotionEffect::amplifier)
    ).apply(i, AppliedPotionEffect::new));
    public static final Codec<Weapon> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("attackSpeed").forGetter(Weapon::attackSpeed),
            APPLIED_POTION_EFFECTS.listOf().optionalFieldOf("randomEffects", List.of()).forGetter(Weapon::randomEffects)
    ).apply(i, Weapon::new));

}
