package unnamed.mmo.chat;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ChatMessage(
        @NotNull Instant timestamp,
        @NotNull String serverId,
        @NotNull String channelId,
        @NotNull UUID sender,
        @NotNull String message
) {

}
