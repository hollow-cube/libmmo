package unnamed.mmo.item.crafting;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;
import org.jetbrains.annotations.NotNull;

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
        for (Integer i : CRAFTING_INDICES) {
            stacks[i] = super.getItemStack(i);
        }
        return stacks;
    }

    @Override
    public boolean leftClick(@NotNull Player player, int slot) {
        if (!isValidIndex(slot)) return false;

        // Only allow tools to be placed into the index
        if (slot == TOOL_INDEX && getItemStack(TOOL_INDEX).isAir() && !getCursorItem(player).isAir() && !isToolItem(getCursorItem(player)))
            return false;
        // Deny placements into the Crafting index
        if (slot == OUTPUT_INDEX && getItemStack(OUTPUT_INDEX).isAir()) return false;

        // Crafting
        if (slot == OUTPUT_INDEX && !getItemStack(OUTPUT_INDEX).isAir()) {
            if (!getCursorItem(player).isAir() || getItemStack(OUTPUT_INDEX).isSimilar(getCursorItem(player))) {
                if (getCursorItem(player).isAir()) {
                    setCursorItem(player, getItemStack(OUTPUT_INDEX));
                } else {
                    setCursorItem(player, getCursorItem(player).withAmount(amount -> amount + getItemStack(OUTPUT_INDEX).amount()));
                }
            }
            removeCraftingItems(1);
            updateCraftingRecipe();
            return true;
        }

        boolean result = super.leftClick(player, slot);
        if (result)
            updateCraftingRecipe();
        return result;
    }

    @Override
    public boolean rightClick(@NotNull Player player, int slot) {
        if (!isValidIndex(slot)) return false;

        // Only allow tools to be placed into the index
        if (slot == TOOL_INDEX && getItemStack(TOOL_INDEX).isAir() && !getCursorItem(player).isAir() && !isToolItem(getCursorItem(player)))
            return false;
        // Deny placements into the Crafting index
        if (slot == OUTPUT_INDEX && getItemStack(OUTPUT_INDEX).isAir()) return false;

        // Crafting
        if (slot == OUTPUT_INDEX && !getItemStack(OUTPUT_INDEX).isAir()) {
            if (!getCursorItem(player).isAir() || getItemStack(OUTPUT_INDEX).isSimilar(getCursorItem(player))) {
                if (getCursorItem(player).isAir()) {
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
        if (result)
            updateCraftingRecipe();
        return result;
    }

    @Override
    public boolean shiftClick(@NotNull Player player, int slot) {
        if (!isValidIndex(slot)) return false;

        final boolean isInWindow = slot <= getSize();
        // Pain and misery

        // If we shift click in player inventory, try to populate it into the correct field
        if (!isInWindow) {
            int playerSlot = PlayerInventoryUtils.convertSlot(slot, getSize());
            ItemStack stack = player.getInventory().getItemStack(playerSlot);
            if (isToolItem(stack) && getItemStack(TOOL_INDEX).isAir()) {
                // Try to shift click into the tool slot
                var result = clickProcessor.shiftClick(player.getInventory(), this, TOOL_INDEX, TOOL_INDEX + 1, 1, player, playerSlot, stack, getCursorItem(player));
                return !result.isCancel();
            } else {
                // Try to shift click into the crafting inventory
                for (int i = 0; i < 9; i += 3) {
                    // Since the crafting slots are not continuous, we have to repeat this call multiple times
                    var result = clickProcessor.shiftClick(player.getInventory(), this, CRAFTING_INDICES.get(i), CRAFTING_INDICES.get(i + 2), 1, player, playerSlot, stack, getCursorItem(player));
                    if (!result.isCancel()) return true;
                }
                return false;
            }
        } else {
            if (slot == OUTPUT_INDEX && !getItemStack(OUTPUT_INDEX).isAir()) {
                // Crafting
                final int maxCrafts = maximumCrafts();
                final ItemStack craftingItemStack = getItemStack(OUTPUT_INDEX);
                // We have calculated the most number of items we can craft, see if we can insert into the inventory
                int outputAmount = craftingItemStack.amount();
                int totalItems = maxCrafts * outputAmount;
                boolean result = player.getInventory().addItemStack(craftingItemStack.withAmount(totalItems), TransactionOption.DRY_RUN);
                if (result) {
                    // We can add all items! go ahead and do so
                    player.getInventory().addItemStack(craftingItemStack.withAmount(totalItems), TransactionOption.ALL);
                    removeCraftingItems(maxCrafts);
                } else {
                    // Figure out how many we can add
                    for (int i = totalItems; i > 0; i -= outputAmount) {
                        boolean canAdd = player.getInventory().addItemStack(craftingItemStack.withAmount(i), TransactionOption.DRY_RUN);
                        if (canAdd) {
                            player.getInventory().addItemStack(craftingItemStack.withAmount(totalItems), TransactionOption.ALL);
                            final int craftNum = totalItems / outputAmount;
                            removeCraftingItems(craftNum);
                            break;
                        }
                    }
                }
                updateCraftingRecipe();
                return true;
            }
        }


        boolean result = super.shiftClick(player, slot);
        if (result)
            updateCraftingRecipe();
        return result;
    }

    private boolean isValidIndex(int i) {
        if (i < getSize())
            return i == TOOL_INDEX || i == OUTPUT_INDEX || CRAFTING_INDICES.contains(i);
        return true;
    }

    private boolean isToolItem(ItemStack itemStack) {
        return true;
    }

    private void updateCraftingRecipe() {
        List<ItemStack> currentRecipe = new ArrayList<>(9);
        for (Integer i : CRAFTING_INDICES) {
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
     *
     * @param recipeCrafts The amount of times to remove the recipe components (that is, the number of crafts that have
     *                     been performed)
     */
    private void removeCraftingItems(int recipeCrafts) {
        if (activeRecipe instanceof ShapedCraftingRecipe recipe) {
            for (int i = 0; i < CRAFTING_INDICES.size(); i++) {
                int index = CRAFTING_INDICES.get(i);
                CraftingRecipe.ComponentEntry entry = recipe.recipe().get(i);
                final int decrementAmount = entry.count() * recipeCrafts;
                if (getItemStack(index).amount() <= decrementAmount) {
                    setItemStack(index, ItemStack.AIR);
                } else {
                    setItemStack(index, getItemStack(index).withAmount(count -> count - decrementAmount));
                }
            }
        } else if (activeRecipe instanceof ToolShapedCraftingRecipe recipe) {
            for (int i = 0; i < CRAFTING_INDICES.size(); i++) {
                int index = CRAFTING_INDICES.get(i);
                CraftingRecipe.ComponentEntry entry = recipe.recipe().get(i);
                final int decrementAmount = entry.count() * recipeCrafts;
                if (getItemStack(index).amount() <= decrementAmount) {
                    setItemStack(index, ItemStack.AIR);
                } else {
                    setItemStack(index, getItemStack(index).withAmount(count -> count - decrementAmount));
                }
            }
        }
    }

    /**
     * Based on the items in the current inventory and the active recipe, calculates the maximum number of crafts that
     * can be performed
     *
     * @return
     */
    private int maximumCrafts() {
        if (activeRecipe == null) {
            return 0;
        }
        if (activeRecipe instanceof ShapedCraftingRecipe recipe) {
            int maxCrafts = 999;
            for (int i = 0; i < CRAFTING_INDICES.size(); i++) {
                int currentAmount = getItemStack(CRAFTING_INDICES.get(i)).amount();
                int craftAmount = recipe.recipe().get(i).item().amount();
                maxCrafts = Math.min(maxCrafts, currentAmount / craftAmount);
            }
            return maxCrafts;
        } else if (activeRecipe instanceof ToolShapedCraftingRecipe recipe) {
            int maxCrafts = 999;
            for (int i = 0; i < CRAFTING_INDICES.size(); i++) {
                int currentAmount = getItemStack(CRAFTING_INDICES.get(i)).amount();
                int craftAmount = recipe.recipe().get(i).item().amount();
                maxCrafts = Math.min(maxCrafts, currentAmount / craftAmount);
            }
            return maxCrafts;
        }
        return 0;
    }
}
