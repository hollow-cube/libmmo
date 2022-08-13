package unnamed.mmo.server.dev.tool;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.registry.Resource;

/**
 * Debug tool for interacting with certain game features. Exists only in development server.
 * <p>
 * Known limitations
 * <ul>
 *     <li>Entering/exiting hand is a bit unreliable (but can be improved)</li>
 *     <li>Left clicked entity is never present (but should be added in the future)</li>
 * </ul>
 */
public interface DebugTool extends Resource {

    @Override
    @NotNull NamespaceID namespace();

    @NotNull ItemStack itemStack();


    // Interaction functions

    default void enteredHand(@NotNull Player player, @NotNull ItemStack itemStack) {
    }

    default void exitedHand(@NotNull Player player, @NotNull ItemStack itemStack) {
    }


    default @NotNull ItemStack leftClicked(@NotNull Player player,
                                           @NotNull ItemStack itemStack,
                                           @Nullable Point targetBlock,
                                           @Nullable Entity targetEntity) {
        return itemStack;
    }

    default @NotNull ItemStack rightClicked(@NotNull Player player,
                                            @NotNull ItemStack itemStack,
                                            @Nullable Point targetBlock,
                                            @Nullable Entity targetEntity) {
        return itemStack;
    }
}
