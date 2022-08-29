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
        if (slot == 0) {
            final ItemStack craftingItemStack = getCraftingSlotItem();
            final ItemStack playerCursorStack = getCursorItem(player);
            // Deny placements into crafting slot
            if (!playerCursorStack.isAir() && craftingItemStack.isAir()) {
                return false;
            // If there is a crafting output, grab it
            } else if(!craftingItemStack.isAir() && (
                    craftingItemStack.equals(playerCursorStack) || playerCursorStack.isAir())) {
                if(playerCursorStack.isAir()) {
                    setCursorItem(player, craftingItemStack);
                } else {
                    setCursorItem(player, playerCursorStack.withAmount(amount -> amount + craftingItemStack.amount()));
                }
                // Decrement all items in the crafting menu by 1
                for (int i = CRAFTING_INVENTORY_START_INDEX; i <= CRAFTING_INVENTORY_END_INDEX; i++) {
                    ItemStack componentStack = getItemStack(i);
                    if(componentStack.isAir()) continue;
                    int count = componentStack.amount() - 1;
                    if (count <= 0) {
                        setItemStack(i, ItemStack.AIR);
                    } else {
                        setItemStack(i, getItemStack(i).withAmount(count));
                    }
                }
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
                } else if(playerCursorStack.equals(craftingItemStack)) {
                    setCursorItem(player, playerCursorStack.withAmount(integer -> integer + craftingItemStack.amount()));
                }
                // Decrement all items in the crafting menu by 1
                for (int i = 1; i <= 9; i++) {
                    int count = getItemStack(i).amount() - 1;
                    if (count <= 0) {
                        setItemStack(i, ItemStack.AIR);
                    } else {
                        setItemStack(i, getItemStack(i).withAmount(count));
                    }
                }
                updateCraftingRecipe();
                return true;
            }
        }
        boolean result = super.rightClick(player, slot);
        // If the click is canceled, don't proceed
        if (!result) {
            return false;
        }
        if (slot <= 9) {
            // Item was placed into crafting menu, check recipes
            updateCraftingRecipe();
        }
        return true;
    }

    @Override
    public boolean shiftClick(@NotNull Player player, int slot) {
        // Shift click - will work as long as you can shift click it into an empty player slot
        final ItemStack craftingItemStack = getCraftingSlotItem();
        final ItemStack playerCursorStack = getCursorItem(player);
        if (slot == 0 && !craftingItemStack.isAir()) {
            int maxCrafts = 999;
            for (int i = CRAFTING_INVENTORY_START_INDEX; i <= CRAFTING_INVENTORY_END_INDEX; i++) {
                // TODO: This does not consider if more than one item is consumed in a craft
                maxCrafts = Math.min(maxCrafts, getItemStack(i).amount());
            }
            final int maximumCrafts = maxCrafts;
            // We have calculated the most number of items we can craft, see if we can insert into the inventory
            int outputAmount = getItemStack(0).amount();
            int totalItems = maxCrafts * outputAmount;
            boolean result = player.getInventory().addItemStack(getItemStack(0).withAmount(totalItems), TransactionOption.DRY_RUN);
            if(result) {
                // We can add all items! go ahead and do so
                player.getInventory().addItemStack(getItemStack(0).withAmount(totalItems), TransactionOption.ALL);
                for (int i = 1; i <= 9; i++) {
                    // TODO: This does not consider if more than one item is consumed in a craft
                    if(getItemStack(i).amount() <= maximumCrafts) {
                        setItemStack(i, ItemStack.AIR);
                    } else {
                        setItemStack(i, getItemStack(i).withAmount(integer -> integer - maximumCrafts));
                    }
                }
            } else {
                // Figure out how many we can add
                for(int i = totalItems; i > 0; i -= outputAmount) {
                    boolean canAdd = player.getInventory().addItemStack(getItemStack(0).withAmount(i), TransactionOption.DRY_RUN);
                    if(canAdd) {
                        player.getInventory().addItemStack(getItemStack(0).withAmount(totalItems), TransactionOption.ALL);
                        final int craftNum = totalItems / outputAmount;
                        for (int j = CRAFTING_INVENTORY_START_INDEX; j <= CRAFTING_INVENTORY_END_INDEX; j++) {
                            // TODO: This does not consider if more than one item is consumed in a craft
                            if(getItemStack(i).amount() <= craftNum) {
                                setItemStack(i, ItemStack.AIR);
                            } else {
                                setItemStack(i, getItemStack(i).withAmount(integer -> integer - craftNum));
                            }
                        }
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
        final boolean isInWindow = slot <= 9;
        final int clickSlot = isInWindow ? slot : PlayerInventoryUtils.convertSlot(slot, 10);
        final ItemStack clicked = isInWindow ? getItemStack(slot) : playerInventory.getItemStack(clickSlot);
        final ItemStack cursor = getCursorItem(player); // Isn't used in the algorithm
        final InventoryClickResult clickResult = this.clickProcessor.shiftClick(
                isInWindow ? this : playerInventory,
                isInWindow ? playerInventory : this,
                1, isInWindow ? playerInventory.getInnerSize() : getInnerSize(), 1,
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
        List<ItemStack> currentRecipe = List.of(Arrays.copyOfRange(getItemStacks(), 1, 9));
        for (CraftingRecipe recipe : recipeList.getRecipeList()) {
            if (recipe.doesRecipeMatch(currentRecipe)) {
                setItemStack(CRAFTING_SLOT, recipe.getRecipeOutput());
                activeRecipe = recipe;
                return;
            }
        }
        activeRecipe = null;
        setItemStack(0, ItemStack.AIR);
    }

    private void updateAll(Player player) {
        player.getInventory().update();
        update(player);
    }

    private ItemStack getCraftingSlotItem() {
        return getItemStack(CRAFTING_SLOT);
    }

    @TestOnly
    public void refreshCurrentRecipe() {
        updateCraftingRecipe();
    }
}