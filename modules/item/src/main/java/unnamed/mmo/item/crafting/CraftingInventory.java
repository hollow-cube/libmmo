package unnamed.mmo.item.crafting;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class CraftingInventory extends Inventory {
    public CraftingInventory() {
        super(InventoryType.CRAFTING, Component.text("Ultimate Supreme Crafting Menu"));
    }
}
