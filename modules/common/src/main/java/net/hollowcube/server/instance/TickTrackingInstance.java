package net.hollowcube.server.instance;

import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TickTrackingInstance extends InstanceContainer {
    //todo i (matt) really dont think this should be in common, but not sure where

    private long tick = 0;

    public TickTrackingInstance(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType, @Nullable IChunkLoader loader) {
        super(uniqueId, dimensionType, loader);
    }

    public TickTrackingInstance(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
        super(uniqueId, dimensionType);
    }

    @Override
    public void tick(long time) {
        tick++;

        super.tick(time);
    }

    public long getTick() {
        return tick;
    }
}
