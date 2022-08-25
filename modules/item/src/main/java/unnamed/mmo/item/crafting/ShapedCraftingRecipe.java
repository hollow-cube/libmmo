package unnamed.mmo.item.crafting;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ShapedCraftingRecipe(@NotNull List<Material> recipe, @NotNull ItemStack output) implements CraftingRecipe {

    public ShapedCraftingRecipe {
        if(recipe.size() > 9) {
            throw new IllegalArgumentException("Cannot create a shaped recipe with more than 9 items!");
        }
    }

    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        for (int i = 0; i < recipe.size(); i++) {
            if(recipe.get(i) == Material.AIR) continue;
            if(recipe.get(i) != items.get(i).material()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }
}
