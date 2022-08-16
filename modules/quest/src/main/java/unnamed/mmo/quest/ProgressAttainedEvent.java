package unnamed.mmo.quest;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ProgressAttainedEvent implements PlayerEvent {

    private final String progressId;
    private final Player player;

    public ProgressAttainedEvent(Player player, String id) {
        this.player = player;
        this.progressId = id;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public String getProgressId() {
        return progressId;
    }
}
