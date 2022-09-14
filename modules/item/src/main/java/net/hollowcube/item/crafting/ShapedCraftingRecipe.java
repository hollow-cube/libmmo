package net.hollowcube.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.dfu.ExtraCodecs;
import net.hollowcube.item.Item;
import net.hollowcube.item.ItemImpl;

import java.util.List;

public record ShapedCraftingRecipe(@NotNull List<ComponentEntry> recipe,
                                   @NotNull ItemStack output) implements CraftingRecipe {

    public ShapedCraftingRecipe {
        if (recipe.size() != 9) {
            throw new IllegalArgumentException("Shaped crafting recipe does not have exactly 9 items (use air for empty slots)!");
        }
    }

    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        for (int i = 0; i < recipe.size(); i++) {
            if (recipe.get(i).item() == ItemImpl.EMPTY_ITEM) continue;
            if (recipe.get(i).item().stateId() != Item.fromItemStack(items.get(i)).stateId() || items.get(i).amount() < recipe.get(i).count()) {
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
        int stateId = Item.fromItemStack(itemStack).stateId();
        for (ComponentEntry entry : recipe) {
            if (entry.item().stateId() != stateId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean requiresTool() {
        return false;
    }


    public static final Codec<ShapedCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            ENTRY_CODEC.listOf().fieldOf("components").forGetter(ShapedCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ShapedCraftingRecipe::output)
    ).apply(i, ShapedCraftingRecipe::new));
}
