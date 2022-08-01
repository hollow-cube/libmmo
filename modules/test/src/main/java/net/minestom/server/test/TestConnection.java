package net.minestom.server.test;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.packet.server.ServerPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TestConnection {
    @NotNull CompletableFuture<@NotNull Player> connect(@NotNull PlayerProvider playerProvider, @NotNull Instance instance, @NotNull Pos pos);

    <T extends ServerPacket> @NotNull Collector<T> trackIncoming(@NotNull Class<T> type);

    default @NotNull Collector<ServerPacket> trackIncoming() {
        return trackIncoming(ServerPacket.class);
    }
}
