package net.hollowcube.blocks.resource;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.hollowcube.blocks.resource.handler.ResourceSpawner;
import net.hollowcube.blocks.schem.SchematicManager;
import net.hollowcube.player.event.PlayerLongDiggingStartEvent;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.utils.Rotation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

@AutoService(Facet.class)
public class ResourceFacet implements Facet {

    // Globally increasing ID for resources in the world
    private final AtomicInteger nextId = new AtomicInteger(0);

    // All resources currently in the server
    // todo probably would make more sense to do this per instance rather than per server
    private final Int2ObjectMap<WorldResource> resources = Int2ObjectMaps.synchronize(new Int2ObjectArrayMap<>());

    private final BlockHandler resourceSpawnerHandler = new ResourceSpawner(this);

    //todo ResourceSpawner block handler which will delay and then spawn a resource in a given position
    // added to the world when a resource is removed.


    @Override
    public void hook(@NotNull ServerWrapper server) {
        var node = EventNode.all("awghauwghuiawg");
        node.addListener(PlayerLongDiggingStartEvent.class, this::handleDigging);
        node.addListener(PlayerBlockBreakEvent.class, this::blockBroken);
        server.addEventNode(node);

        server.registerBlockHandler(() -> resourceSpawnerHandler);
    }

    public void addResourceSpawner(@NotNull Resource resource, @NotNull Instance instance, @NotNull Point pos) {
        var block = instance.getBlock(pos)
                .withTag(Resource.TAG, resource.name())
                .withHandler(resourceSpawnerHandler);
        instance.setBlock(pos, block);
    }

    public void spawnResource(@NotNull Resource resource, @NotNull Instance instance, @NotNull Point pos) {
        var worldResource = new WorldResource(nextId.getAndIncrement(), resource, instance, pos);
        resources.put(worldResource.id(), worldResource);

        //todo i guess this logic could exist in MultiBlockResource? This is not very generic
        if (resource instanceof MultiBlockResource res) {
            var schematicName = res.schematics().get(worldResource.id() % res.schematics().size());
            var schematic = SchematicManager.get(schematicName);
            var rotation = Rotation.values()[(worldResource.id() % 4) * 2];

            schematic.applyManual(rotation, (blockPos, block) -> {
                var taggedBlock = block.withTag(WorldResource.ID_TAG, worldResource.id());
                instance.setBlock(pos.add(blockPos), taggedBlock);
            });
        }

    }

    private void handleDigging(@NotNull PlayerLongDiggingStartEvent event) {
        final var block = event.getBlock();
        final var resourceId = block.getTag(WorldResource.ID_TAG);
        if (resourceId == null) return;

        //todo get health from resource
        event.setDiggingBlock(5, () -> 1);
    }

    private void blockBroken(@NotNull PlayerBlockBreakEvent event) {
        final var instance = event.getInstance();
        final var block = event.getBlock();
        final var resourceId = block.getTag(WorldResource.ID_TAG);
        if (resourceId == null) return;

        var worldResource = resources.get((int) resourceId);
        resources.remove((int) resourceId);

        final var pos = worldResource.pos();
        final var res = worldResource.resource();
        if (res instanceof MultiBlockResource resource) {
            //todo this might be problematic for "randomness" (getting both schematic and rotation from this number)
            var schematicName = resource.schematics().get(resourceId % resource.schematics().size());
            var schematic = SchematicManager.get(schematicName);
            var rotation = Rotation.values()[(resourceId % 4) * 2];

            schematic.applyManual(rotation, (relBlockPos, b) -> {
                final var blockPos = pos.add(relBlockPos);

                // Spawn particle
                ServerPacket packet = ParticleCreator.createParticlePacket(
                        Particle.BLOCK, false, blockPos.x(), blockPos.y(), blockPos.z(), 0.51f, 0.51f, 0.51f,
                        0.15f, 25, binaryWriter -> binaryWriter.writeVarInt(b.stateId()));
                event.getPlayer().sendPacketToViewersAndSelf(packet);

                // Remove the block
                instance.setBlock(blockPos, Block.AIR);
            });

            //todo add the resource back to the world after a delay
        }

        // Add a resource spawner block at the position
        addResourceSpawner(res, instance, pos);
    }
}
