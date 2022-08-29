package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;

public record ShapedCraftingRecipe(@NotNull List<ComponentEntry> recipe, @NotNull ItemStack output) implements CraftingRecipe {

    public ShapedCraftingRecipe {
        if(recipe.size() != 9) {
            throw new IllegalArgumentException("Shaped crafting recipe does not have exactly 9 items (use air for empty slots)!");
        }
    }

    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        for (int i = 0; i < recipe.size(); i++) {
            if (recipe.get(i).material() == Material.AIR) continue;
            if (recipe.get(i).material() != items.get(i).material() || items.get(i).amount() >= recipe.get(i).count()) {
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
        for(ComponentEntry entry : recipe) {
            if(itemStack.material() == entry.material()) {
                return true;
            }
        }
        return false;
    }



    public static final Codec<ShapedCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            ENTRY_CODEC.listOf().fieldOf("components").forGetter(ShapedCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ShapedCraftingRecipe::output)
    ).apply(i, ShapedCraftingRecipe::new));
}
