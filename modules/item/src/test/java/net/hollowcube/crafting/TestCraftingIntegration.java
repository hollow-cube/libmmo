package net.hollowcube.crafting;

import net.hollowcube.item.test.MockItem;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;
import net.hollowcube.item.crafting.CraftingInventory;
import net.hollowcube.item.crafting.CraftingRecipe;
import net.hollowcube.item.crafting.RecipeList;
import net.hollowcube.item.crafting.ShapelessCraftingRecipe;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnvTest
public class TestCraftingIntegration {

    private final MockItem log = new MockItem(NamespaceID.from("test", "log"), 2, 2, null, Material.OAK_LOG, 1);

    @Test
    public void testCraftAction(Env env) {
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));
        RecipeList list = new RecipeList();
        list.addRecipes(List.of(new ShapelessCraftingRecipe(List.of(new CraftingRecipe.ComponentEntry(log, 1)), ItemStack.of(Material.OAK_PLANKS).withAmount(4))));
        CraftingInventory inventory = new CraftingInventory(list);
        inventory.setItemStack(9, log.asItemStack().withAmount(2));
        inventory.refreshCurrentRecipe();
        assertEquals(Material.OAK_PLANKS, inventory.getItemStack(0).material());
        player.openInventory(inventory);
        inventory.leftClick(player, 0);
        assertEquals(ItemStack.of(Material.OAK_PLANKS, 4), inventory.getCursorItem(player));
        assertEquals(log.asItemStack().withAmount(1), inventory.getItemStack(9));
        inventory.leftClick(player, 0);
        assertEquals(ItemStack.of(Material.OAK_PLANKS, 8), inventory.getCursorItem(player));
        assertEquals(inventory.getItemStack(9), ItemStack.AIR);
    }
}
