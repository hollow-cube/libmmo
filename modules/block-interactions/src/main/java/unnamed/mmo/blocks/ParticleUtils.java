package unnamed.mmo.blocks;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;

public class ParticleUtils {

    public static void spawnBlockBreakParticles(Player player, Point point, Material material) {
        ServerPacket packet = ParticleCreator.createParticlePacket(
                Particle.BLOCK, false, point.x(), point.y(), point.z(), 0.01f, 0.01f, 0.01f,
                0.05f, 12, binaryWriter -> binaryWriter.writeVarInt(material.id()));
        player.sendPacketToViewersAndSelf(packet);
    }
}