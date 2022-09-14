package net.hollowcube.damage;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.attribute.ItemAttribute;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hollowcube.damage.iticks.ImmunityTicks;
import net.hollowcube.damage.iticks.ImmunityTicksPlayerImpl;

public class DamageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DamageProcessor.class);
    // Probably don't want everything to be static here, change?
    private static final ImmunityTicks iTickManager = new ImmunityTicksPlayerImpl();
    private static final AttackCooldown attackCooldownManager = new AttackCooldown();

    public static void init() {
        MinecraftServer.getGlobalEventHandler().addListener(EntityAttackEvent.class, event -> processDamage(event.getEntity(), event.getTarget()));
        MinecraftServer.getSchedulerManager().scheduleTask(iTickManager::update, TaskSchedule.immediate(), TaskSchedule.tick(1));
    }

    public static void processDamage(@NotNull Entity source, @NotNull Entity target) {
        if (target instanceof LivingEntity targetEntity) {
            if (iTickManager.isEntityImmune(targetEntity)) return;

            if (source instanceof LivingEntity attacker) {
                DamageType type = DamageType.fromEntity(attacker);
                DamageInfo info = new DamageInfo(type, getAttributeValue(attacker, Attribute.ATTACK_DAMAGE, 1));
                // Handle Items - Attack damage attribute was handled by getAttributeValue previously
                // Only apply enchants on main hand
                /*ItemStack itemStack = player.getItemInMainHand();
                if (itemStack.meta().getEnchantmentMap().containsKey(Enchantment.SHARPNESS)) {
                    info.getDamageValue().addBase(0.5 + 0.5 * itemStack.meta().getEnchantmentMap().get(Enchantment.SHARPNESS));
                }
                if (itemStack.meta().getEnchantmentMap().containsKey(Enchantment.FIRE_ASPECT)) {
                    info.setFireTicks(80 * itemStack.meta().getEnchantmentMap().get(Enchantment.FIRE_ASPECT));
                }
                if (itemStack.meta().getEnchantmentMap().containsKey(Enchantment.KNOCKBACK)) {
                    info.getKnockbackStrength().addBase(0.5 * itemStack.meta().getEnchantmentMap().get(Enchantment.KNOCKBACK));
                }*/
                // More knockback if sprinting
                if (attacker.isSprinting()) {
                    info.getKnockbackStrength().multiply(1.15);
                }
                // Handle Potion effects
                /*int potionModifier = 0;
                for (TimedPotion potionEffect : player.getActiveEffects()) {
                    if (potionEffect.getPotion().effect() == PotionEffect.WEAKNESS) {
                        potionModifier -= 4 * potionEffect.getPotion().amplifier();
                    } else if (potionEffect.getPotion().effect() == PotionEffect.STRENGTH) {
                        potionModifier += 3 * potionEffect.getPotion().amplifier();
                    }
                }
                info.getDamageValue().addBase(potionModifier);*/

                // Enemy Armor Multiplier
                double armorValue = getAttributeValue(targetEntity, Attribute.ARMOR, 0).getFinalValue();
                double armorToughnessValue = getAttributeValue(targetEntity, Attribute.ARMOR_TOUGHNESS, 0).getFinalValue();
                // Formula from https://minecraft.fandom.com/wiki/Armor#Damage_protection
                info.getDamageValue().multiply(
                        1 -
                                Math.min(20,
                                        Math.max(armorValue / 5,
                                                armorValue - (4 * info.getDamageValue().getFinalValue()) / (armorToughnessValue + 8)))
                                        / 25
                );

                // Enemy Potion Effect multiplier
                /*double resistanceMod = 1;
                for (TimedPotion potionEffect : player.getActiveEffects()) {
                    if (potionEffect.getPotion().effect() == PotionEffect.RESISTANCE) {
                        resistanceMod -= 0.2 * potionEffect.getPotion().amplifier();
                        // Bound resistance
                        resistanceMod = Math.max(0, resistanceMod);
                    }
                }
                info.getDamageValue().multiply(resistanceMod);*/
                // Attack cooldown modifier
                if (attacker instanceof Player player) {
                    info.getDamageValue().multiply(attackCooldownManager.getCooldownDamageMultiplier(player));
                    attackCooldownManager.resetCooldown(player);
                }
                info.apply(targetEntity, attacker.getPosition().yaw());
                iTickManager.setImmunityTicks(targetEntity, info.getImmunityTicks());
            }
        } else {
            logger.warn("Non-living entity was attacked?!?");
        }
    }

    private static @NotNull MultiPartValue getAttributeValue(@NotNull LivingEntity entity, @NotNull Attribute attribute, double baseAttributeValue) {
        MultiPartValue value = new MultiPartValue(baseAttributeValue);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = entity.getEquipment(slot);
            // Attributes
            for (ItemAttribute itemAttribute : item.meta().getAttributes()) {
                if (itemAttribute.attribute().equals(attribute)) {
                    value.modifyValue(itemAttribute.amount(), itemAttribute.operation());
                }
            }
        }
        return value;
    }
}
