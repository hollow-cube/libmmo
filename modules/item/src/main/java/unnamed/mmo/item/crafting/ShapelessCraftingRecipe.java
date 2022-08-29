package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.List;

public record ShapelessCraftingRecipe(@NotNull List<ComponentEntry> recipe, @NotNull ItemStack output) implements CraftingRecipe {

    public ShapelessCraftingRecipe {
        if(recipe.size() > 9) {
            throw new IllegalArgumentException("Cannot create a shapeless recipe with more than 9 items!");
        }
    }

    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        ArrayList<ComponentEntry> recipeClone = new ArrayList<>(recipe);
        for (ItemStack item : items) {
            if (item.isAir()) continue;
            // O(n)2 time, :(
            // But I don't think there's a better way
            recipeClone.removeIf(entry -> item.material() == entry.material() && item.amount() == entry.count());
        }
        return recipeClone.isEmpty();
    }
    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public boolean containsIngredient(@NotNull ItemStack itemStack) {
        for(ComponentEntry entry : recipe) {
            if(entry.material() == itemStack.material()) {
                return true;
            }
        }
        return false;
    }


    public static final Codec<ShapelessCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            ENTRY_CODEC.listOf().fieldOf("components").forGetter(ShapelessCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ShapelessCraftingRecipe::output)
    ).apply(i, ShapelessCraftingRecipe::new));
}
