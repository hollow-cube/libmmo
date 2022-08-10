package unnamed.mmo.damage;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.attribute.ItemAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DamageProcesser {

    private static final Logger logger = LoggerFactory.getLogger(DamageProcesser.class);

    public static void registerEvents() {
        MinecraftServer.getGlobalEventHandler().addListener(EntityAttackEvent.class, event -> processDamage(event.getEntity(), event.getTarget()));
    }

    public static void processDamage(Entity source, Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            if (source instanceof Player player) {
                DamageType type = DamageType.fromPlayer(player);
                // Set variables
                double damage = 1; // Base damage
                double multiplyBase = 1; // For operation 1 (Multiply_Base)
                double multiplyAmount = 1; // For Operation 2 (Multiply)
                int fireTicks = 0;
                float kbStrength = 0.4f;

                // More knockback if sprinting
                if(player.isSprinting()) {
                    kbStrength *= 1.15;
                }
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack item = player.getEquipment(slot);
                    // Attributes
                    for (ItemAttribute attribute : item.meta().getAttributes()) {
                        if(attribute.attribute() == Attribute.ATTACK_DAMAGE) {
                            switch (attribute.operation()) {
                                case ADDITION -> damage += attribute.amount();
                                case MULTIPLY_BASE -> multiplyBase += attribute.amount();
                                case MULTIPLY_TOTAL -> multiplyAmount *= attribute.amount();
                            }
                        }
                    }
                    // Only apply enchants on main hand
                    if (slot == EquipmentSlot.MAIN_HAND) {
                        if(item.meta().getEnchantmentMap().containsKey(Enchantment.SHARPNESS)) {
                            damage += 0.5 + 0.5 * item.meta().getEnchantmentMap().get(Enchantment.SHARPNESS);
                        }
                        if(item.meta().getEnchantmentMap().containsKey(Enchantment.FIRE_ASPECT)) {
                            fireTicks = 80 * item.meta().getEnchantmentMap().get(Enchantment.FIRE_ASPECT);
                        }
                        if(item.meta().getEnchantmentMap().containsKey(Enchantment.KNOCKBACK)) {
                            kbStrength += 0.5f * item.meta().getEnchantmentMap().get(Enchantment.KNOCKBACK);
                        }
                    }
                    // Apply attributes
                    damage = damage * multiplyBase;
                    damage = damage * multiplyAmount;
                }
                // TODO - Handle defending entity armor

                
                // Why are attributes in double amounts, but damage is in float?
                livingEntity.damage(type, (float) damage);
                if(fireTicks > 0) {
                    livingEntity.setFireForDuration(fireTicks);
                }

                // Apply knockback - don't need to handle kb resistance, since that is already done in livingEntity.takeKnockback
                double yawRadians = player.getPosition().yaw() * Math.PI / 180;
                livingEntity.takeKnockback(kbStrength, Math.sin(yawRadians), -Math.cos(yawRadians));
            } else {
                logger.warn("Non-player entity attacked a target! This is currently unsupported.");
            }
        } else {
            logger.warn("Non-living entity was attacked?!?");
        }
    }
}
