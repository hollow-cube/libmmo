package unnamed.mmo.player;

import com.google.common.truth.Truth;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import org.junit.jupiter.api.Test;
import unnamed.mmo.player.event.PlayerLongDiggingStartEvent;

import static com.google.common.truth.Truth.assertThat;

@EnvTest
public class TestLongDiggingIntegration {

    @Test
    public void testStartDiggingEventNoLongDigging(Env env) {
        var instance = env.createFlatInstance();
        var player = env.createPlayer(PlayerImpl::new, instance, new Pos(0, 42, 0));
        player.setInstance(instance).join();

        // Ensure proper event is triggered
        var listener = env.listen(PlayerLongDiggingStartEvent.class);
        listener.followup(event -> {
            Truth.assertThat(event.getPlayer()).isEqualTo(player);
        });

        var startDiggingPacket = new ClientPlayerDiggingPacket(ClientPlayerDiggingPacket.Status.STARTED_DIGGING,
                new Vec(0, 42, 0), BlockFace.TOP);
        player.addPacketToQueue(startDiggingPacket);

        player.interpretPacketQueue();
    }

    @Test
    public void testLongDiggingHappyCase(Env env) {
        var instance = env.createFlatInstance();
        var connection = env.createConnection();
        var player = connection.connect(PlayerImpl::new, instance, new Pos(0, 42, 0)).join();
        player.setInstance(instance).join();

        // Start digging with 12 health, dealing 1 damage per tick
        var listener = env.listen(PlayerLongDiggingStartEvent.class);
        listener.followup(event -> event.setDiggingBlock(12, () -> 1));

        var animationCollector = connection.trackIncoming(BlockBreakAnimationPacket.class);

        // Start digging
        var startDiggingPacket = new ClientPlayerDiggingPacket(ClientPlayerDiggingPacket.Status.STARTED_DIGGING,
                new Vec(0, 42, 0), BlockFace.TOP);
        player.addPacketToQueue(startDiggingPacket);
        player.interpretPacketQueue();

        // Tick 12 times
        for (int i = 0; i < 12; i++) {
            player.tick(System.currentTimeMillis());
        }

        animationCollector.assertCount(12);
        var animationPackets = animationCollector.collect();
        for (int i = 1; i <= 12; i++) {
            var packet = animationPackets.get(i - 1);
            assertThat(packet.entityId()).isEqualTo(player.getEntityId() + 1);
            if (i < 12) {
                assertThat(packet.destroyStage()).isEqualTo(i);
            } else {
                // Final stage should send 127 because block is now broken
                assertThat(packet.destroyStage()).isEqualTo(Byte.MAX_VALUE);
            }
        }
    }
}
