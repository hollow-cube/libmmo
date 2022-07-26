package net.hollowcube.crafting;

import net.hollowcube.item.crafting.CraftingRecipe;
import net.hollowcube.item.test.MockItem;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;
import net.hollowcube.item.crafting.CraftingInventory;
import net.hollowcube.item.crafting.RecipeList;
import net.hollowcube.item.crafting.ShapelessCraftingRecipe;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestCraftingRecipe {

    private final MockItem log = new MockItem(NamespaceID.from("test", "log"), 2, 2, null, Material.OAK_LOG, 1);

    @Test
    public void testBlankRecipe() {
        MinecraftServer.init();
        RecipeList list = new RecipeList();
        CraftingInventory inventory = new CraftingInventory(list);
        inventory.setItemStack(2, ItemStack.of(Material.OAK_LOG));
        inventory.refreshCurrentRecipe();
        assertEquals(inventory.getItemStack(0), ItemStack.AIR);
    }

    @Test
    public void testPlankRecipe() {
        MinecraftServer.init();
        RecipeList list = new RecipeList();
        CraftingInventory inventory = new CraftingInventory(list);
        list.addRecipes(List.of(new ShapelessCraftingRecipe(List.of(new CraftingRecipe.ComponentEntry(log, 1)), ItemStack.of(Material.OAK_PLANKS))));
        inventory.setItemStack(2, log.asItemStack());
        inventory.refreshCurrentRecipe();
        assertEquals(inventory.getItemStack(1), ItemStack.AIR);
        assertEquals(inventory.getItemStack(2).material(), Material.OAK_LOG);
        assertEquals(inventory.getItemStack(0), ItemStack.of(Material.OAK_PLANKS));
    }

    @Test
    public void testRecipeRemoval() {
        MinecraftServer.init();
        RecipeList list = new RecipeList();
        CraftingInventory inventory = new CraftingInventory(list);
        list.addRecipes(List.of(new ShapelessCraftingRecipe(List.of(new CraftingRecipe.ComponentEntry(log, 1)), ItemStack.of(Material.OAK_PLANKS))));
        inventory.setItemStack(2, log.asItemStack());
        inventory.refreshCurrentRecipe();
        assertEquals(inventory.getItemStack(0), ItemStack.of(Material.OAK_PLANKS));
        inventory.setItemStack(2, ItemStack.AIR);
        inventory.refreshCurrentRecipe();
        assertNotEquals(inventory.getItemStack(0), ItemStack.of(Material.OAK_PLANKS));
        assertEquals(inventory.getItemStack(0), ItemStack.AIR);
    }
}
