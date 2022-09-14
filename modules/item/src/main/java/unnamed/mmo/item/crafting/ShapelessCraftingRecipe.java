package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;
import unnamed.mmo.dfu.ExtraCodecs;

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

            // TODO: This could produce some funny behavior, fix somehow
            // Say for instance you needed 4 of a stick in 1 slot, and 8 of that same stick in another
            // This method could detect you have the 4 required in the items list, and remove the 8 required in the recipe list
            // And then when it checks against the 8 stick itemstack in the recipe, you don't have it in the crafting menu
            // But that's a really weird case to go for, we would need to implement some sort of best-fit algorithm, or just not do that
            recipeClone.removeIf(entry -> Item.fromItemStack(item).stateId() == entry.item().stateId() && item.amount() >= entry.count());
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
            if(Item.fromItemStack(itemStack).stateId() == entry.item().stateId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean requiresTool() {
        return false;
    }


    public static final Codec<ShapelessCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            ENTRY_CODEC.listOf().fieldOf("components").forGetter(ShapelessCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ShapelessCraftingRecipe::output)
    ).apply(i, ShapelessCraftingRecipe::new));
}
