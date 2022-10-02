package net.hollowcube.blocks.resource.handler;

import net.hollowcube.blocks.resource.Resource;
import net.hollowcube.blocks.resource.ResourceFacet;
import net.hollowcube.debug.Simulation;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.thread.TickThread;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class ResourceSpawner implements BlockHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceSpawner.class);

    private final ResourceFacet manager;

    public ResourceSpawner(ResourceFacet manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("starlight:resource_spawner");
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        if (!placement.getBlock().hasTag(Resource.TAG)) {
            var instance = placement.getInstance();
            var block = placement.getBlock();
            var pos = placement.getBlockPosition();

            // Block handler should not be present if the resource tag is not present. Remove it and log error.
            LOGGER.error("Resource spawner present on a block without resource tag: {} at {} in {}", block, pos, instance);
            instance.setBlock(pos, block.withHandler(null));

            //todo ensure the tag is valid
        }
    }

    @Override
    public void tick(@NotNull Tick tick) {
        if (!Simulation.isRunning()) return;

        var thread = TickThread.current();
        if (thread == null) return;

        // Tick once per second
        if (thread.getTick() % 20 != 0) return;
        // 25% chance to spawn each tick
        if (ThreadLocalRandom.current().nextInt(4) != 0) return;

        var block = tick.getBlock();
        var resource = Resource.fromNamespaceId(block.getTag(Resource.TAG));
        assert resource != null; // Checked in placement handler

        var instance = tick.getInstance();
        var pos = tick.getBlockPosition();
        manager.spawnResource(resource, instance, pos);
    }
}
