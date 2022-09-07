package unnamed.mmo.crafting;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemImpl;
import unnamed.mmo.item.crafting.CraftingRecipe;
import unnamed.mmo.item.crafting.RecipeList;
import unnamed.mmo.item.crafting.ToolCraftingInventory;
import unnamed.mmo.item.crafting.ToolShapedCraftingRecipe;
import unnamed.mmo.item.test.MockItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnvTest
public class TestToolCraftingIntegration {

    private final MockItem tool = new MockItem(NamespaceID.from("test", "tool"), 3, 3, null, Material.WOODEN_AXE, 1);
    private final MockItem log = new MockItem(NamespaceID.from("test", "log"), 2, 2, null, Material.OAK_LOG, 1);
    private final MockItem stick = new MockItem(NamespaceID.from("test", "stick"), 4, 4, null, Material.STICK, 1);

    private final ToolShapedCraftingRecipe recipe = new ToolShapedCraftingRecipe(
            tool,
            List.of(new CraftingRecipe.ComponentEntry(ItemImpl.EMPTY_ITEM, 1), new CraftingRecipe.ComponentEntry(ItemImpl.EMPTY_ITEM, 1), new CraftingRecipe.ComponentEntry(ItemImpl.EMPTY_ITEM, 1),
                    new CraftingRecipe.ComponentEntry(ItemImpl.EMPTY_ITEM, 1), new CraftingRecipe.ComponentEntry(log, 1), new CraftingRecipe.ComponentEntry(ItemImpl.EMPTY_ITEM, 1),
                    new CraftingRecipe.ComponentEntry(stick, 1), new CraftingRecipe.ComponentEntry(stick, 1), new CraftingRecipe.ComponentEntry(stick, 1)),
            log.withAmount(16).asItemStack()
    );


    @Test
    public void testToolCraft(Env env) {
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));
        RecipeList list = new RecipeList();
        list.addRecipe(recipe);
        ToolCraftingInventory inventory = new ToolCraftingInventory(list);
        player.openInventory(inventory);
        player.getInventory().setItemStack(5, tool.asItemStack());
        player.getInventory().setItemStack(6, log.asItemStack());
        assertEquals(ItemStack.AIR, inventory.getItemStack(24)); // Output should be empty
        player.getOpenInventory().shiftClick(player, 86); // Shift click tool
        assertEquals(tool.asItemStack(), inventory.getItemStack(19));
        inventory.setCursorItem(player, log.asItemStack());
        player.getOpenInventory().leftClick(player, 21);
        inventory.setCursorItem(player, stick.asItemStack());
        player.getOpenInventory().leftClick(player, 29);
        inventory.setCursorItem(player, stick.asItemStack());
        player.getOpenInventory().leftClick(player, 30);
        inventory.setCursorItem(player, stick.asItemStack());
        player.getOpenInventory().leftClick(player, 31);
        assertEquals(log.withAmount(16).asItemStack(), inventory.getItemStack(24));
    }
}
