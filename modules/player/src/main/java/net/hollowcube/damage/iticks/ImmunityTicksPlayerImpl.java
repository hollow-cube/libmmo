package net.hollowcube.damage.iticks;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class ImmunityTicksPlayerImpl implements ImmunityTicks {
    private final Tag<Long> iTickTag = Tag.Long("immunity-ticks");

    @Override
    public boolean isEntityImmune(@NotNull LivingEntity entity) {
        if (entity.hasTag(iTickTag)) {
            return entity.getTag(iTickTag) > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    @Override
    public void setImmunityTicks(@NotNull LivingEntity entity, int tickCount) {
        if (tickCount <= 0) {
            entity.removeTag(iTickTag);
        } else {
            entity.setTag(iTickTag, System.currentTimeMillis() + (long) MinecraftServer.TICK_MS * tickCount);
        }
    }

    @Override
    public void update() {
        // Nothing
    }
}
