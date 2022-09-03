package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemImpl;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;

public record ToolShapedCraftingRecipe(Item toolItem, @NotNull List<ComponentEntry> recipe, @NotNull ItemStack output) implements CraftingRecipe {
    @Override
    public boolean doesRecipeMatch(@NotNull List<ItemStack> items) {
        if (toolItem.stateId() != Item.fromItemStack(items.get(0)).stateId()) return false;
        for (int i = 1; i < recipe.size(); i++) {
            if (recipe.get(i).item() == ItemImpl.EMPTY_ITEM) continue;
            if (recipe.get(i).item().stateId() != Item.fromItemStack(items.get(i)).stateId() || items.get(i).amount() >= recipe.get(i).count()) {
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
