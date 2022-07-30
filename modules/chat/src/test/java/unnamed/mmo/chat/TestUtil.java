package unnamed.mmo.chat;

import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.UUID;

//todo move me to common test lib
public class TestUtil {

    public static Instant instantNow() {
        return Instant.ofEpochMilli(1659127729952L);
    }

    public static @NotNull Player headlessPlayer(@NotNull String name) {
        return new Player(UUID.randomUUID(), name, EMPTY_PLAYER_CONNECTION);
    }

    public static @NotNull Player headlessPlayer() {
        return headlessPlayer("test0");
    }

    private static final PlayerConnection EMPTY_PLAYER_CONNECTION = new PlayerConnection() {
        @Override
        public void sendPacket(@NotNull SendablePacket packet) {

        }

        @Override
        public @NotNull SocketAddress getRemoteAddress() {
            return new InetSocketAddress(0);
        }
    };

}
