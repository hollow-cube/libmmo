package unnamed.mmo.item.crafting;

import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CraftingRecipe {
    /**
     * Checks against the list of items in the current crafting menu to see if the recipe matches
     * @param items A list of items in the crafting menu, ordered from top row (starting from the left), to bottom right
     * @return true if the recipe matches, false if it does not
     */
    boolean doesRecipeMatch(@NotNull List<ItemStack> items);

    @NotNull ItemStack getRecipeOutput();

    boolean containsIngredient(@NotNull ItemStack itemStack);
}
