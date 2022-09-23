package net.hollowcube.player.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class PlayerBubbleColumnSinkEvent implements PlayerInstanceEvent {
    private final Player player;
    private final int energyLevel;

    public PlayerBubbleColumnSinkEvent(Player player, int energyLevel) {
        this.player = player;
        this.energyLevel = energyLevel;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void cancelPlayerSwimming() {
        if (player.getInstance().getBlock(player.getPosition()) == Block.BUBBLE_COLUMN) {
            player.setFood(6);
        }
    }

    public void restorePlayerEnergy() {
        if (player.getInstance().getBlock(player.getPosition()) != Block.BUBBLE_COLUMN) {
            player.setFood(energyLevel);
        }
    }
}
