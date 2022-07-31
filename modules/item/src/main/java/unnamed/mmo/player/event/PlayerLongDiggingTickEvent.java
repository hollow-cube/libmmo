package unnamed.mmo.player.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class PlayerLongDiggingTickEvent implements PlayerInstanceEvent, BlockEvent {
    private final Player player;
    private final Block block;

    private String id;
    private int damage = 0;

    public PlayerLongDiggingTickEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    @Override
    public @NotNull Block getBlock() {
        return null;
    }

    @Override
    public @NotNull Player getPlayer() {
        return null;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
