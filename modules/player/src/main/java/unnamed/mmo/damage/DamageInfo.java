package unnamed.mmo.damage;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DamageInfo {

    private final MultiPartValue damageValue;
    private final MultiPartValue knockbackStrength;
    private int fireTicks;
    private final DamageType type;

    public DamageInfo(DamageType type) {
        this(type, new MultiPartValue(1));
    }

    public DamageInfo(@NotNull DamageType type, @NotNull MultiPartValue damageValue) {
        this.type = type;
        this.damageValue = damageValue;
        knockbackStrength = new MultiPartValue(0.4);
        fireTicks = 0;
    }

    public @NotNull MultiPartValue getDamageValue() {
        return damageValue;
    }

    public @NotNull MultiPartValue getKnockbackStrength() {
        return knockbackStrength;
    }

    @Contract(mutates = "this")
    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    // TODO: Perhaps make Knockback a vector instead of a MultiPartValue to more easily handle cases when there is no attacker?
    @Contract(mutates = "param1")
    public void apply(@NotNull LivingEntity entity, double attackerYaw) {
        // Deal damage
        // Why are attributes in double amounts, but damage is in float?
        entity.damage(type, (float) damageValue.getFinalValue());
        // Apply knockback - don't need to handle kb resistance, since that is already done in livingEntity.takeKnockback
        double yawRadians =  attackerYaw * Math.PI / 180;
        entity.takeKnockback((float) knockbackStrength.getFinalValue(), Math.sin(yawRadians), -Math.cos(yawRadians));
        // Apply fire
        entity.setFireForDuration(fireTicks);
    }
}
