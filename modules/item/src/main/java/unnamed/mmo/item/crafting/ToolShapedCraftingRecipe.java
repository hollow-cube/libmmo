package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.dfu.ExtraCodecs;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemImpl;

import java.util.List;

public record ToolShapedCraftingRecipe(Item toolItem, @NotNull List<ComponentEntry> recipe,
                                       @NotNull ItemStack output) implements CraftingRecipe {

    public ToolShapedCraftingRecipe {
        if (recipe.size() != 9) {
            throw new IllegalArgumentException("Shaped crafting recipe does not have exactly 9 items (use air for empty slots)!");
        }
    }

    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        if (toolItem.stateId() != Item.fromItemStack(items.get(0)).stateId()) return false;
        for (int i = 1; i < recipe.size(); i++) {
            if (recipe.get(i).item() == ItemImpl.EMPTY_ITEM) continue;
            // Need to do i + 1 since tool is the first item
            if (recipe.get(i).item().stateId() != Item.fromItemStack(items.get(i + 1)).stateId() || items.get(i + 1).amount() < recipe.get(i).count()) {
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
        int stackStateId = Item.fromItemStack(itemStack).stateId();
        if (toolItem.stateId() == stackStateId) return true;
        for (ComponentEntry entry : recipe) {
            if (entry.item().stateId() != stackStateId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean requiresTool() {
        return true;
    }

    public static final Codec<ToolShapedCraftingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            Item.CODEC.fieldOf("tool").forGetter(ToolShapedCraftingRecipe::toolItem),
            ENTRY_CODEC.listOf().fieldOf("components").forGetter(ToolShapedCraftingRecipe::recipe),
            ExtraCodecs.MATERIAL.fieldOf("output").xmap(ItemStack::of, ItemStack::material).forGetter(ToolShapedCraftingRecipe::output)
    ).apply(i, ToolShapedCraftingRecipe::new));
}
