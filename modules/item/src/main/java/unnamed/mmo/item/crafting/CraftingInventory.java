package unnamed.mmo.item.crafting;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;
import java.util.List;

public class CraftingInventory extends Inventory {

    private final RecipeList recipeList;

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
            // Deny placements into crafting slot
            if (!getCursorItem(player).isAir()) {
                return false;
            // If there is a crafting output, grab it
            } else if (!getItemStack(0).isAir()) {
                setCursorItem(player, getItemStack(0));
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
        boolean result = super.leftClick(player, slot);
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
    public boolean rightClick(@NotNull Player player, int slot) {
        // Somewhat similar to left-click - but if the itemstacks match, you can pick up more
        if (slot == 0) {
            // Deny placements into crafting slot
            if (!getCursorItem(player).isAir() && getItemStack(0).isAir()) {
                return false;
                // If there is a crafting output, and you have the same itemstack on your cursor or air, grab it
            } else if (!getItemStack(0).isAir()) {
                if(getCursorItem(player).isAir()) {
                    setCursorItem(player, getItemStack(0));
                } else if(getCursorItem(player).equals(getItemStack(0))) {
                    setCursorItem(player, getCursorItem(player).withAmount(integer -> integer + getItemStack(0).amount()));
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
        return super.shiftClick(player, slot);
    }

    private void updateCraftingRecipe() {
        List<ItemStack> currentRecipe = List.of(Arrays.copyOfRange(getItemStacks(), 1, 9));
        for (CraftingRecipe recipe : recipeList.getRecipeList()) {
            if (recipe.doesRecipeMatch(currentRecipe)) {
                setItemStack(0, recipe.getRecipeOutput());
                return;
            }
        }
        setItemStack(0, ItemStack.AIR);
    }

    @TestOnly
    public void refreshCurrentRecipe() {
        updateCraftingRecipe();
    }
}
