package unnamed.mmo.blocks.ore.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.ore.Ore;

public class PlayerOreBreakEvent implements PlayerInstanceEvent, BlockEvent {
    private final Player player;
    private final Block block;
    private final Ore ore;

    public PlayerOreBreakEvent(Player player, Block block, Ore ore) {
        this.player = player;
        this.block = block;
        this.ore = ore;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Block getBlock() {
        return block;
    }

    public @NotNull Ore getOre() {
        return ore;
    }
}
