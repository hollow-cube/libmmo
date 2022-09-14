package unnamed.mmo.damage.iticks;

import net.minestom.server.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ImmunityTicks {
    /**
     * Determines if this entity is currently immune from damage
     *
     * @param entity The entity to check
     * @return True if this entity cannot be damaged, false if it can
     */
    boolean isEntityImmune(@NotNull LivingEntity entity);

    /**
     * Sets the amount of immunity ticks this entity will have
     *
     * @param entity    The entity to make immune
     * @param tickCount The number of ticks to make this entity immune
     */
    void setImmunityTicks(@NotNull LivingEntity entity, int tickCount);

    // Called every tick
    void update();
}
