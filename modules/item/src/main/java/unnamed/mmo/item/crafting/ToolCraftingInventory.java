package unnamed.mmo.item.crafting;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolCraftingInventory extends Inventory {

    private final RecipeList recipeList;
    private CraftingRecipe activeRecipe = null;

    private final int TOOL_INDEX = 19;
    private final List<Integer> CRAFTING_INDICIES = List.of(11, 12, 13, 20, 21, 22, 29, 30, 31);
    private final int OUTPUT_INDEX = 24;

    public ToolCraftingInventory(RecipeList list) {
        super(InventoryType.CHEST_6_ROW, Component.text("Tool Crafting Inventory"));
        this.recipeList = list;
    }


    @Override
    public @NotNull ItemStack[] getItemStacks() {
        ItemStack[] stacks = new ItemStack[getSize()];
        ItemStack blockedItemStack = ItemStack.of(Material.BLACK_STAINED_GLASS_PANE);
        Arrays.fill(stacks, blockedItemStack);
        stacks[TOOL_INDEX] = super.getItemStack(TOOL_INDEX);
        stacks[OUTPUT_INDEX] = super.getItemStack(OUTPUT_INDEX);
        for(Integer i : CRAFTING_INDICIES) {
            stacks[i] = super.getItemStack(i);
        }
        return stacks;
    }

    @Override
    public boolean leftClick(@NotNull Player player, int slot) {
        if(!isValidIndex(slot)) return false;

        // Only allow tools to be placed into the index
        if(slot == TOOL_INDEX && getItemStack(TOOL_INDEX).isAir() && !getCursorItem(player).isAir() && isToolItem(getCursorItem(player))) return true;
        // Deny placements into the Crafting index
        if(slot == OUTPUT_INDEX && getItemStack(OUTPUT_INDEX).isAir()) return false;

        // Crafting
        if(slot == OUTPUT_INDEX && !getItemStack(OUTPUT_INDEX).isAir()) {

        }

        boolean result = super.leftClick(player, slot);
        if(result)
            updateCraftingRecipe();
        return result;
    }

    @Override
    public boolean rightClick(@NotNull Player player, int slot) {
        if(!isValidIndex(slot)) return false;



        boolean result = super.rightClick(player, slot);
        if(result)
            updateCraftingRecipe();
        return result;
    }

    @Override
    public boolean shiftClick(@NotNull Player player, int slot) {
        if(!isValidIndex(slot)) return false;
        return super.shiftClick(player, slot);
    }

    private boolean isValidIndex(int i) {
        if(i < getSize())
            return i == TOOL_INDEX || i == OUTPUT_INDEX || CRAFTING_INDICIES.contains(i);
        return true;
    }

    private boolean isToolItem(ItemStack itemStack) {
        return true;
    }

    private void updateCraftingRecipe() {
        List<ItemStack> currentRecipe = new ArrayList<>(9);
        for(Integer i : CRAFTING_INDICIES) {
            currentRecipe.add(getItemStack(i));
        }
        List<ItemStack> currentRecipeWithTool = new ArrayList<>(10);
        currentRecipeWithTool.add(getItemStack(TOOL_INDEX));
        currentRecipeWithTool.addAll(currentRecipe);
        for (CraftingRecipe recipe : recipeList.getRecipeList()) {
            if (recipe.requiresTool()) {
                if (recipe.doesRecipeMatch(currentRecipeWithTool)) {
                    setItemStack(OUTPUT_INDEX, recipe.getRecipeOutput());
                    activeRecipe = recipe;
                    return;
                }
            } else {
                if (recipe.doesRecipeMatch(currentRecipe)) {
                    setItemStack(OUTPUT_INDEX, recipe.getRecipeOutput());
                    activeRecipe = recipe;
                    return;
                }
            }
        }
        activeRecipe = null;
        setItemStack(OUTPUT_INDEX, ItemStack.AIR);
    }

}
