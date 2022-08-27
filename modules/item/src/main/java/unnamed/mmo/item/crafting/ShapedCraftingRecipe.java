package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.util.ExtraCodecs;

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
            if (recipe.get(i) == Material.AIR) continue;
            if (recipe.get(i) != items.get(i).material()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public boolean containsIngredient(@NotNull ItemStack itemStack) {
        for(Material material : recipe) {
            if(itemStack.material() == material) {
                return true;
            }
        }
        return false;
    }

    public static final Codec<ShapedCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.MATERIAL.listOf().fieldOf("components").forGetter(ShapedCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ShapedCraftingRecipe::output)
    ).apply(i, ShapedCraftingRecipe::new));
}
