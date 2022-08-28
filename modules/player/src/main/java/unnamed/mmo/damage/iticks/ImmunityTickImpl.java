package unnamed.mmo.damage.iticks;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ImmunityTickImpl implements ImmunityTicks {

    // Map of current time
    public HashMap<LivingEntity, TickEntry> tickMap = new HashMap<>();

    @Override
    public boolean isEntityImmune(@NotNull LivingEntity entity) {
        return tickMap.containsKey(entity);
    }

    @Override
    public void setImmunityTicks(@NotNull LivingEntity entity, int tickCount) {
        tickMap.put(entity, new TickEntry(System.currentTimeMillis(), tickCount));
    }

    @Override
    public void update() {
        tickMap.entrySet().removeIf(entry -> entry.getValue().startTime() + (long) MinecraftServer.TICK_MS * entry.getValue().ticksImmune() < System.currentTimeMillis());
    }

    private record TickEntry(long startTime, int ticksImmune) {}
}
