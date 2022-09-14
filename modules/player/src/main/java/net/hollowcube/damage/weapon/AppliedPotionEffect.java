package net.hollowcube.damage.weapon;

import net.minestom.server.potion.PotionEffect;

public record AppliedPotionEffect(
        double chance,
        PotionEffect effect,
        int tickDuration,
        int amplifier
) {}
