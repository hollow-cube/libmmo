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
    private final List<Integer> CRAFTING_INDICES = List.of(11, 12, 13, 20, 21, 22, 29, 30, 31);
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
        for(Integer i : CRAFTING_INDICES) {
            stacks[i] = super.getItemStack(i);
        }
        return stacks;
    }

    @Override
    public boolean leftClick(@NotNull Player player, int slot) {
        if(!isValidIndex(slot)) return false;

        // Only allow tools to be placed into the index
        if(slot == TOOL_INDEX && getItemStack(TOOL_INDEX).isAir() && !getCursorItem(player).isAir() && !isToolItem(getCursorItem(player))) return false;
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

        // Only allow tools to be placed into the index
        if(slot == TOOL_INDEX && getItemStack(TOOL_INDEX).isAir() && !getCursorItem(player).isAir() && !isToolItem(getCursorItem(player))) return false;
        // Deny placements into the Crafting index
        if(slot == OUTPUT_INDEX && getItemStack(OUTPUT_INDEX).isAir()) return false;

        // Crafting
        if(slot == OUTPUT_INDEX && !getItemStack(OUTPUT_INDEX).isAir()) {
            if(!getCursorItem(player).isAir() || getItemStack (OUTPUT_INDEX).isSimilar(getCursorItem(player))) {
                if(getCursorItem(player).isAir()) {
                    setCursorItem(player, getItemStack(OUTPUT_INDEX));
                } else {
                    setCursorItem(player, getCursorItem(player).withAmount(amount -> amount + getItemStack(OUTPUT_INDEX).amount()));
                }
            }
            removeCraftingItems(1);
            updateCraftingRecipe();
            return true;
        }

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
            return i == TOOL_INDEX || i == OUTPUT_INDEX || CRAFTING_INDICES.contains(i);
        return true;
    }

    private boolean isToolItem(ItemStack itemStack) {
        return true;
    }

    private void updateCraftingRecipe() {
        List<ItemStack> currentRecipe = new ArrayList<>(9);
        for(Integer i : CRAFTING_INDICES) {
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

    /**
     * Removes the current recipe's crafting components from the inventory's item stacks
     * @param recipeCrafts The amount of times to remove the recipe components (that is, the number of crafts that have been performed)
     */
    private void removeCraftingItems(int recipeCrafts) {
        if(activeRecipe instanceof ShapedCraftingRecipe recipe) {
            for(int i = 0; i < CRAFTING_INDICES.size(); i++) {
                int index = CRAFTING_INDICES.get(i);
                CraftingRecipe.ComponentEntry entry = recipe.recipe().get(i);
                final int decrementAmount = entry.count() * recipeCrafts;
                if(getItemStack(index).amount() <= decrementAmount) {
                    setItemStack(index, ItemStack.AIR);
                } else {
                    setItemStack(index, getItemStack(index).withAmount(count -> count - decrementAmount));
                }
            }
        } else if (activeRecipe instanceof ToolShapedCraftingRecipe recipe) {
            for(int i = 0; i < CRAFTING_INDICES.size(); i++) {
                int index = CRAFTING_INDICES.get(i);
                CraftingRecipe.ComponentEntry entry = recipe.recipe().get(i);
                final int decrementAmount = entry.count() * recipeCrafts;
                if(getItemStack(index).amount() <= decrementAmount) {
                    setItemStack(index, ItemStack.AIR);
                } else {
                    setItemStack(index, getItemStack(index).withAmount(count -> count - decrementAmount));
                }
            }
        }
    }

}
