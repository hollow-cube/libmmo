package net.hollowcube.damage;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.event.trait.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityKilledByEntityEvent implements EntityEvent {

    private final LivingEntity target;
    private final Entity attacker;

    public EntityKilledByEntityEvent(@NotNull LivingEntity target, @NotNull Entity attacker) {
        this.target = target;
        this.attacker = attacker;
    }

    @Override
    public @NotNull Entity getEntity() {
        return target;
    }

    public @NotNull Entity getAttacker() {
        return attacker;
    }
}
