package net.hollowcube.player.damage;

import net.hollowcube.damage.DamageProcessor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.attribute.AttributeSlot;
import net.minestom.server.item.attribute.ItemAttribute;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestDamageIntegration {

    @Test
    public void testCanApplyDamage() {
        // Required for not NullPointering when applying damage because of an event call
        MinecraftServer.init();

        LivingEntity attacker = new LivingEntity(EntityType.ZOMBIE);
        LivingEntity target = new LivingEntity(EntityType.ZOMBIE);
        // When creating entities like above, health starts out at 1 ???
        target.setHealth(target.getMaxHealth());
        DamageProcessor.processDamage(attacker, target);
        assertEquals(target.getMaxHealth() - 1, target.getHealth());
    }

    @Test
    public void testWeaponAttack() {
        // Required for not NullPointering when applying damage because of an event call
        MinecraftServer.init();

        LivingEntity attacker = new LivingEntity(EntityType.ZOMBIE);
        LivingEntity target = new LivingEntity(EntityType.ZOMBIE);
        target.setHealth(target.getMaxHealth());
        attacker.setItemInMainHand(ItemStack.builder(
                Material.DIAMOND
        ).meta(meta -> meta.attributes(
                List.of(new ItemAttribute(UUID.randomUUID(), "damage", Attribute.ATTACK_DAMAGE, AttributeOperation.ADDITION, 10, AttributeSlot.MAINHAND))
        )).build());
        DamageProcessor.processDamage(attacker, target);
        // Base damage of 1, +10 from diamond
        assertEquals(target.getMaxHealth() - 11, target.getHealth());
    }
}
