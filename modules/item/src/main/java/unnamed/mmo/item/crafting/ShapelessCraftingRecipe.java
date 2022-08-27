package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.util.ExtraCodecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ShapelessCraftingRecipe(@NotNull Map<Material, Integer> recipe, @NotNull ItemStack output) implements CraftingRecipe {

    public ShapelessCraftingRecipe {
        int materialCount = recipe.values().stream().mapToInt(Integer::intValue).sum();
        if(materialCount > 9) {
            throw new IllegalArgumentException("Cannot create a shapeless recipe with more than 9 items!");
        }
    }

    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        Map<Material, Integer> counts = new HashMap<>();
        for (ItemStack item : items) {
            if (item.isAir()) continue;
            if (!recipe.containsKey(item.material())) {
                return false;
            }
            counts.putIfAbsent(item.material(), 0);
            counts.put(item.material(), counts.get(item.material()) + 1);
        }
        return counts.equals(recipe);
    }
    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public boolean containsIngredient(@NotNull ItemStack itemStack) {
        return recipe.containsKey(itemStack.material());
    }


    public static final Codec<ShapelessCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(ExtraCodecs.MATERIAL, Codec.INT).fieldOf("components").forGetter(ShapelessCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ShapelessCraftingRecipe::output)
    ).apply(i, ShapelessCraftingRecipe::new));
}
