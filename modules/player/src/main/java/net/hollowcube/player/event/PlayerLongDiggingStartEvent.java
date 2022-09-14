package net.hollowcube.player.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntSupplier;

public class PlayerLongDiggingStartEvent implements PlayerInstanceEvent, BlockEvent {
    private final Player player;
    private final Block block;

    private int maxHealth = 0;
    private IntSupplier damageFunction = null;

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

    public int getMaxHealth() {
        return maxHealth;
    }

    public @Nullable IntSupplier getDamageFunction() {
        return damageFunction;
    }

    public void setDiggingBlock(int maxHealth, IntSupplier damageFunction) {
        this.maxHealth = maxHealth;
        this.damageFunction = damageFunction;
    }
}
