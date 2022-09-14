package net.hollowcube.damage;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class AttackCooldown {

    private final Tag<Long> startTimeTag = Tag.Long("cooldown-start-time");
    private final Tag<Integer> cooldownTickTag = Tag.Integer("cooldown-ticks");

    public void resetCooldown(Player player) {
        // From https://minecraft.fandom.com/wiki/Damage#Attack_cooldown
        float attackSpeed = player.getAttributeValue(Attribute.ATTACK_SPEED);
        int cooldownTicks = (int) (20f / attackSpeed);
        player.setTag(startTimeTag, System.currentTimeMillis());
        player.setTag(cooldownTickTag, cooldownTicks);
    }

    public double getCooldownDamageMultiplier(Player player) {
        // Formula from https://minecraft.fandom.com/wiki/Damage#Attack_cooldown
        if (!player.hasTag(startTimeTag) || !player.hasTag(cooldownTickTag)) {
            return 1;
        }
        int cooldownTicks = player.getTag(cooldownTickTag);
        int ticksSinceLastAttacked = (int) ((System.currentTimeMillis() - player.getTag(startTimeTag)) / MinecraftServer.TICK_MS);
        return (0.2 + Math.pow(((ticksSinceLastAttacked + 0.5) / cooldownTicks), 2) * 0.8);
    }
}