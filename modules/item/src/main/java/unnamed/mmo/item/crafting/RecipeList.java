package unnamed.mmo.item.crafting;

import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecipeList {

    private final List<CraftingRecipe> recipeList = new ArrayList<>();

    public void addRecipes(List<CraftingRecipe> list) {
        recipeList.addAll(list);
    }

    public @NotNull List<CraftingRecipe> getRecipeList() {
        return recipeList;
    }

    public @NotNull List<CraftingRecipe> findRecipesWithIngredient(@NotNull ItemStack itemStack) {
        ArrayList<CraftingRecipe> list = new ArrayList<>();
        for(CraftingRecipe recipe : recipeList) {
            if(recipe.containsIngredient(itemStack)) {
                list.add(recipe);
            }
        }
        return list;
    }

    public @Nullable CraftingRecipe findRecipeForItem(@NotNull ItemStack itemStack) {
        for(CraftingRecipe recipe : recipeList) {
            if(recipe.getRecipeOutput().equals(itemStack)) {
                return recipe;
            }
        }
        return null;
    }
}