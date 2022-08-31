package unnamed.mmo.item.crafting;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.click.InventoryClickResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import unnamed.mmo.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingInventory extends Inventory {

    private final RecipeList recipeList;
    private CraftingRecipe activeRecipe = null;

    private static final int CRAFTING_SLOT = 0;
    private static final int CRAFTING_INVENTORY_START_INDEX = 1;
    private static final int CRAFTING_INVENTORY_END_INDEX = 9;

    // The crafting menu indices are laid out as follows
    // 1 2 3
    // 4 5 6 -> 0
    // 7 8 9
    public CraftingInventory(RecipeList list) {
        super(InventoryType.CRAFTING, Component.text("Ultimate Supreme Crafting Menu"));
        this.recipeList = list;
    }

    @Override
    public boolean leftClick(@NotNull Player player, int slot) {
        if (slot == CRAFTING_SLOT) {
            final ItemStack craftingItemStack = getCraftingSlotItem();
            final ItemStack playerCursorStack = getCursorItem(player);
            // Deny placements into crafting slot
            if (!playerCursorStack.isAir() && craftingItemStack.isAir()) {
                return false;
            // If there is a crafting output, grab it
            } else if(!craftingItemStack.isAir() && (
                    craftingItemStack.isSimilar(playerCursorStack) || playerCursorStack.isAir())) {
                if(playerCursorStack.isAir()) {
                    setCursorItem(player, craftingItemStack);
                } else {
                    setCursorItem(player, playerCursorStack.withAmount(amount -> amount + craftingItemStack.amount()));
                }
                removeCraftingItems(1);
                updateCraftingRecipe();
                return true;
            }
        }
        boolean result = super.leftClick(player, slot);
        // If the click is canceled, don't proceed
        if (!result) {
            return false;
        }
        if (slot <= CRAFTING_INVENTORY_END_INDEX) {
            // Item was placed into crafting menu, check recipes
            updateCraftingRecipe();
        }
        return true;
    }

    @Override
    public boolean rightClick(@NotNull Player player, int slot) {
        // Somewhat similar to left-click - but if the itemstacks match, you can pick up more
        if (slot == CRAFTING_SLOT) {
            final ItemStack craftingItemStack = getCraftingSlotItem();
            final ItemStack playerCursorStack = getCursorItem(player);
            // Deny placements into crafting slot
            if (!playerCursorStack.isAir() && craftingItemStack.isAir()) {
                return false;
                // If there is a crafting output, and you have the same itemstack on your cursor or air, grab it
            } else if (!craftingItemStack.isAir()) {
                if(getCursorItem(player).isAir()) {
                    setCursorItem(player, craftingItemStack);
                } else if(playerCursorStack.isSimilar(craftingItemStack)) {
                    setCursorItem(player, playerCursorStack.withAmount(integer -> integer + craftingItemStack.amount()));
                }
                removeCraftingItems(1);
                updateCraftingRecipe();
                return true;
            }
        }
        boolean result = super.rightClick(player, slot);
        // If the click is canceled, don't proceed
        if (!result) {
            return false;
        }
        if (slot <= CRAFTING_INVENTORY_END_INDEX) {
            // Item was placed into crafting menu, check recipes
            updateCraftingRecipe();
        }
        return true;
    }

    @Override
    public boolean shiftClick(@NotNull Player player, int slot) {
        // Shift click - will work as long as you can shift click it into an empty player slot
        final ItemStack craftingItemStack = getCraftingSlotItem();
        if (slot == CRAFTING_SLOT && !craftingItemStack.isAir()) {
            final int maxCrafts = maximumCrafts();
            // We have calculated the most number of items we can craft, see if we can insert into the inventory
            int outputAmount = craftingItemStack.amount();
            int totalItems = maxCrafts * outputAmount;
            boolean result = player.getInventory().addItemStack(craftingItemStack.withAmount(totalItems), TransactionOption.DRY_RUN);
            if(result) {
                // We can add all items! go ahead and do so
                player.getInventory().addItemStack(craftingItemStack.withAmount(totalItems), TransactionOption.ALL);
                removeCraftingItems(maxCrafts);
            } else {
                // Figure out how many we can add
                for(int i = totalItems; i > 0; i -= outputAmount) {
                    boolean canAdd = player.getInventory().addItemStack(craftingItemStack.withAmount(i), TransactionOption.DRY_RUN);
                    if(canAdd) {
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
        // And then we copy the code from Inventory#shiftClick, except we start our clickProcessor search at 1 instead to avoid
        // shift clicking things into the crafting slot
        final PlayerInventory playerInventory = player.getInventory();
        final boolean isInWindow = slot <= CRAFTING_INVENTORY_END_INDEX;
        final int clickSlot = isInWindow ? slot : PlayerInventoryUtils.convertSlot(slot, CRAFTING_INVENTORY_END_INDEX + 1);
        final ItemStack clicked = isInWindow ? getItemStack(slot) : playerInventory.getItemStack(clickSlot);
        final ItemStack cursor = getCursorItem(player); // Isn't used in the algorithm
        final InventoryClickResult clickResult = this.clickProcessor.shiftClick(
                isInWindow ? this : playerInventory,
                isInWindow ? playerInventory : this,
                CRAFTING_INVENTORY_START_INDEX, isInWindow ? playerInventory.getInnerSize() : getInnerSize(), 1,
                player, clickSlot, clicked, cursor);
        if (clickResult.isCancel()) {
            updateAll(player);
            return false;
        }
        if (isInWindow) {
            setItemStack(slot, clickResult.getClicked());
        } else {
            playerInventory.setItemStack(clickSlot, clickResult.getClicked());
        }
        updateAll(player); // FIXME: currently not properly client-predicted
        setCursorItem(player, clickResult.getCursor());
        return true;
    }

    private void updateCraftingRecipe() {
        List<ItemStack> currentRecipe = List.of(Arrays.copyOfRange(getItemStacks(), CRAFTING_INVENTORY_START_INDEX, CRAFTING_INVENTORY_END_INDEX + 1));
        for (CraftingRecipe recipe : recipeList.getRecipeList()) {
            if (recipe.doesRecipeMatch(currentRecipe)) {
                setItemStack(CRAFTING_SLOT, recipe.getRecipeOutput());
                activeRecipe = recipe;
                return;
            }
        }
        activeRecipe = null;
        setItemStack(CRAFTING_SLOT, ItemStack.AIR);
    }

    private void updateAll(Player player) {
        player.getInventory().update();
        update(player);
    }

    private ItemStack getCraftingSlotItem() {
        return getItemStack(CRAFTING_SLOT);
    }

    /**
     * Removes the current recipe's crafting components from the inventory's item stacks
     * @param recipeCrafts The amount of times to remove the recipe components (that is, the number of crafts that have been performed)
     */
    private void removeCraftingItems(int recipeCrafts) {
        if(activeRecipe instanceof ShapedCraftingRecipe recipe) {
            for(int i = CRAFTING_INVENTORY_START_INDEX; i <= CRAFTING_INVENTORY_END_INDEX; i++) {
                CraftingRecipe.ComponentEntry entry = recipe.recipe().get(i - 1); // Shift index down 1 because 0 is the output slot
                final int decrementAmount = entry.count() * recipeCrafts;
                if(getItemStack(i).amount() <= decrementAmount) {
                    setItemStack(i, ItemStack.AIR);
                } else {
                    setItemStack(i, getItemStack(i).withAmount(count -> count - decrementAmount));
                }
            }
        } else if (activeRecipe instanceof ShapelessCraftingRecipe recipe) {
            ArrayList<CraftingRecipe.ComponentEntry> recipeClone = new ArrayList<>(recipe.recipe());
            for (int i = CRAFTING_INVENTORY_START_INDEX; i <= CRAFTING_INVENTORY_END_INDEX; i++) {
                if (!getItemStack(i).isAir()) {
                    // Since we don't know exactly what pairs with what, we will have to do a bit of searching
                    var iterator = recipeClone.iterator();
                    while(iterator.hasNext()) {
                        CraftingRecipe.ComponentEntry entry = iterator.next();
                        final int decrementAmount = entry.count() * recipeCrafts;
                        if(entry.item().stateId() == Item.fromItemStack(getItemStack(i)).stateId() && getItemStack(i).amount() >= decrementAmount) {
                            // Found match, Decrement
                            if(getItemStack(i).amount() <= decrementAmount) {
                                setItemStack(i, ItemStack.AIR);
                            } else {
                                setItemStack(i, getItemStack(i).withAmount(count -> count - decrementAmount));
                            }
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * Based on the items in the current inventory and the active recipe, calculates the maximum number of crafts that can be performed
     * @return
     */
    private int maximumCrafts() {
        if(activeRecipe == null) {
            return 0;
        }
        if(activeRecipe instanceof ShapedCraftingRecipe recipe) {
            int maxCrafts = 999;
            for (int i = CRAFTING_INVENTORY_START_INDEX; i <= CRAFTING_INVENTORY_END_INDEX; i++) {
                int currentAmount = getItemStack(i).amount();
                int craftAmount = recipe.recipe().get(i - 1).item().amount();
                maxCrafts = Math.min(maxCrafts, currentAmount / craftAmount);
            }
            return maxCrafts;
        } else if(activeRecipe instanceof ShapelessCraftingRecipe recipe) {
            int maxCrafts = 999;
            for(int i = CRAFTING_INVENTORY_START_INDEX; i <= CRAFTING_INVENTORY_END_INDEX; i++) {
                for(var componentEntry : recipe.recipe()) {
                    if(componentEntry.item().stateId() == Item.fromItemStack(getItemStack(i)).stateId()) {
                        maxCrafts = Math.min(maxCrafts, getItemStack(i).amount() / componentEntry.item().amount());
                    }
                }
            }
            return maxCrafts;
        }
        return 0;
    }

    @TestOnly
    public void refreshCurrentRecipe() {
        updateCraftingRecipe();
    }
}
