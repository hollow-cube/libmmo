package unnamed.mmo.player.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLongDiggingStartEvent implements PlayerInstanceEvent, BlockEvent {
    private final Player player;
    private final Block block;

    private String id = null;
    private int maxHealth = 0;

    public PlayerLongDiggingStartEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    @Override
    public @NotNull Block getBlock() {
        return block;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public @Nullable String getBreakId() {
        return id;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setDiggingBlock(@NotNull String id, int maxHealth) {
        this.id = id;
        this.maxHealth = maxHealth;
    }
}
