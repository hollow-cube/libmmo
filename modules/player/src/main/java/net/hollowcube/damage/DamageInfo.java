package net.hollowcube.damage;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.damage.EntityDamage;
import net.minestom.server.event.EventDispatcher;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DamageInfo {

    private final MultiPartValue damageValue;
    private final MultiPartValue knockbackStrength;
    private int fireTicks;
    private final DamageType type;
    private int immunityTicks;

    public DamageInfo(DamageType type) {
        this(type, new MultiPartValue(1));
    }

    public DamageInfo(@NotNull DamageType type, @NotNull MultiPartValue damageValue) {
        this.type = type;
        this.damageValue = damageValue;
        knockbackStrength = new MultiPartValue(0.4);
        fireTicks = 0;
        immunityTicks = 10;
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

    @Contract(mutates = "this")
    public void setImmunityTicks(int immunityTicks) {
        this.immunityTicks = immunityTicks;
    }

    public int getImmunityTicks() {
        return immunityTicks;
    }

    // TODO: Perhaps make Knockback a vector instead of a MultiPartValue to more easily handle cases when there is no attacker?
    @Contract(mutates = "param1")
    public void apply(@NotNull LivingEntity entity, double attackerYaw) {
        // Apply knockback - don't need to handle kb resistance, since that is already done in livingEntity.takeKnockback
        double yawRadians = attackerYaw * Math.PI / 180;
        entity.takeKnockback((float) knockbackStrength.getFinalValue(), Math.sin(yawRadians), -Math.cos(yawRadians));
        // Apply fire
        entity.setFireForDuration(fireTicks);
        // Deal damage

        float finalDamage = (float) damageValue.getFinalValue();
        if (entity.getHealth() - finalDamage <= 0.00001 && type instanceof EntityDamage entityDamage) {
            EntityKilledByEntityEvent entityKilledByEntityEvent = new EntityKilledByEntityEvent(entity, entityDamage.getSource());
            EventDispatcher.call(entityKilledByEntityEvent);
        }
        entity.damage(type, finalDamage);
    }
}
