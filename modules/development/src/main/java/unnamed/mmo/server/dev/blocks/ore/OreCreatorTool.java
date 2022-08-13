package unnamed.mmo.server.dev.blocks.ore;

import com.google.auto.service.AutoService;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.blocks.ore.Ore;
import unnamed.mmo.server.dev.tool.DebugTool;
import unnamed.mmo.util.ComponentUtil;

import java.util.Comparator;
import java.util.List;

@AutoService(DebugTool.class)
public class OreCreatorTool implements DebugTool {
    private static final Tag<String> SELECTION_TAG = Tag.String("ore_creator/selection");

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:ore_creator");
    }

    @Override
    public @NotNull ItemStack itemStack() {
        return ItemStack.builder(Material.DIAMOND)
                .displayName(ComponentUtil.fromStringSafe("Ore Creator"))
                .lore(
                        ComponentUtil.fromStringSafe("<white>Selection: None"),
                        ComponentUtil.fromStringSafe("<white>Shift+RMB: Select ore"),
                        ComponentUtil.fromStringSafe("<white>RMB: Assign selected ore")
                )
                .build();
    }

    @Override
    public @NotNull ItemStack rightClicked(@NotNull Player player, @NotNull ItemStack itemStack, @Nullable Point targetBlock, @Nullable Entity targetEntity) {
        return player.isSneaking() ?
                openOreSelector(player, itemStack) :
                assignOre(player, itemStack, targetBlock);
    }

    private ItemStack assignOre(Player player, ItemStack itemStack, Point targetBlock) {
        if (targetBlock == null)
            return itemStack;

        final Ore ore = Ore.fromNamespaceId(itemStack.getTag(SELECTION_TAG));
        if (ore == null) {
            player.sendMessage("You must select an ore before placing it.");
            return itemStack;
        }

        final Instance instance = player.getInstance();
        instance.setBlock(targetBlock, ore.asBlock());
        player.sendMessage("Set " + targetBlock + " to " + ore.name() + "!");
        return itemStack;
    }

    private ItemStack openOreSelector(Player player, ItemStack itemStack) {
        final Inventory oreSelector = new Inventory(InventoryType.CHEST_2_ROW, "Ore Selector");
        oreSelector.addInventoryCondition(this::selectOre);
        Ore.REGISTRY.values().stream()
                .sorted(Comparator.comparing(Ore::name))
                .forEach(ore -> {
                    //todo is there a better Block->Material conversion
                    Material oreMaterial = Material.fromNamespaceId(ore.oreBlock().namespace());
                    if (oreMaterial == null) oreMaterial = Material.STONE;
                    final ItemStack oreItem = ItemStack.builder(oreMaterial)
                            .displayName(ComponentUtil.fromStringSafe(ore.name()))
                            .build()
                            .withTag(SELECTION_TAG, ore.name());
                    oreSelector.addItemStack(oreItem);
                });
        player.openInventory(oreSelector);
        return itemStack;
    }

    private void selectOre(@NotNull Player player, int slot, @NotNull ClickType clickType, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        if (clickType != ClickType.LEFT_CLICK) return;

        final Inventory inventory = player.getOpenInventory();
        if (inventory == null) return;
        final ItemStack clickedItem = inventory.getItemStack(slot);
        if (clickedItem.isAir()) return;

        //todo is it safe to just use main hand here? is rightCLick triggered on off hand? probably!
        final ItemStack newItemStack = player.getItemInMainHand()
                .withTag(SELECTION_TAG, clickedItem.getTag(SELECTION_TAG))
                .withLore(List.of(
                        //todo centralize this lore addition/itemStack creation
                        ComponentUtil.fromStringSafe("<white>Selection: " + clickedItem.getTag(SELECTION_TAG)),
                        ComponentUtil.fromStringSafe("<white>Shift+RMB: Select ore"),
                        ComponentUtil.fromStringSafe("<white>RMB: Assign selected ore")
                ));
        player.setItemInMainHand(newItemStack);
        player.closeInventory();
    }
}
