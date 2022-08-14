package unnamed.mmo.server.dev.tool;

import com.google.auto.service.AutoService;
import net.minestom.server.ServerProcess;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unnamed.mmo.registry.Registry;
import unnamed.mmo.server.Facet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoService(Facet.class)
public class DebugToolManager implements Facet {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugToolManager.class);

    private static final Registry<DebugTool> REGISTRY = Registry.service("debug_tool", DebugTool.class);

    private static final Tag<String> DEBUG_TOOL_TAG = Tag.String("debug_tool");

    private final EventNode<Event> eventNode = EventNode.all("debug_tool");

    // Hand animation debouncing
    private final Map<UUID, Long> debounce = new HashMap<>();

    public static @NotNull ItemStack createTool(@NotNull String namespace) {
        final DebugTool tool = REGISTRY.get(namespace);
        return tool.itemStack().withTag(DEBUG_TOOL_TAG, tool.name());
    }

    @Override
    public void hook(@NotNull ServerProcess server) {
        // Right click
        eventNode.addListener(PlayerUseItemEvent.class, this::useItemOnAir);
        eventNode.addListener(PlayerUseItemOnBlockEvent.class, this::useItemOnBlock);
        eventNode.addListener(PlayerEntityInteractEvent.class, this::useItemOnEntity);
        // Left click
        eventNode.addListener(PlayerHandAnimationEvent.class, this::handAnimation);
        // Item in hand
        eventNode.addListener(PlayerChangeHeldSlotEvent.class, this::changeHeldSlot);
        // Maintenance
        eventNode.addListener(PlayerSwapItemEvent.class, this::swapItem);
        eventNode.addListener(PlayerDisconnectEvent.class, this::disconnect);

        server.eventHandler().addChild(eventNode);

        LOGGER.info("Loaded {} debug tools", REGISTRY.size());
    }

    private void useItemOnAir(@NotNull PlayerUseItemEvent event) {
        if (event.getHand() != Player.Hand.MAIN) return;

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemStack();
        final DebugTool tool = REGISTRY.get(itemStack.getTag(DEBUG_TOOL_TAG));
        if (tool == null || debounce(player)) return;

        final ItemStack newItemStack = tool.rightClicked(player, itemStack, null, null);
        if (itemStack != newItemStack) {
            player.setItemInHand(event.getHand(), itemStack);
        }
    }

    private void useItemOnBlock(@NotNull PlayerUseItemOnBlockEvent event) {
        if (event.getHand() != Player.Hand.MAIN) return;

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemStack();
        final DebugTool tool = REGISTRY.get(itemStack.getTag(DEBUG_TOOL_TAG));
        if (tool == null || debounce(player)) return;

        final ItemStack newItemStack = tool.rightClicked(player, itemStack, event.getPosition(), null);
        if (itemStack != newItemStack) {
            player.setItemInHand(event.getHand(), itemStack);
        }
    }

    private void useItemOnEntity(@NotNull PlayerEntityInteractEvent event) {
        if (event.getHand() != Player.Hand.MAIN) return;

        final Player player = event.getPlayer();
        final ItemStack itemStack = player.getItemInHand(event.getHand());
        final DebugTool tool = REGISTRY.get(itemStack.getTag(DEBUG_TOOL_TAG));
        if (tool == null || debounce(player)) return;

        final ItemStack newItemStack = tool.rightClicked(player, itemStack, null, event.getTarget());
        if (itemStack != newItemStack) {
            player.setItemInHand(event.getHand(), itemStack);
        }
    }


    private void handAnimation(@NotNull PlayerHandAnimationEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = player.getItemInHand(event.getHand());
        final DebugTool tool = REGISTRY.get(itemStack.getTag(DEBUG_TOOL_TAG));
        if (tool == null || debounce(player)) return;

        final Point targetBlock = player.getTargetBlockPosition(3);
        //todo ray trace for target entity

        final ItemStack newItemStack = tool.leftClicked(player, itemStack, targetBlock, null);
        if (itemStack != newItemStack) {
            player.setItemInHand(event.getHand(), itemStack);
        }
    }


    private void changeHeldSlot(@NotNull PlayerChangeHeldSlotEvent event) {
        final Player player = event.getPlayer();
        //todo pretty sure this needs to check if the swap happened in the held slot or not.

        // Deselect old tool
        final ItemStack fromItem = player.getInventory().getItemStack(player.getHeldSlot());
        final DebugTool fromTool = REGISTRY.get(fromItem.getTag(DEBUG_TOOL_TAG));
        if (fromTool != null) {
            fromTool.exitedHand(player, fromItem);
        }

        final ItemStack toItem = player.getInventory().getItemStack(event.getSlot());
        final DebugTool toTool = REGISTRY.get(toItem.getTag(DEBUG_TOOL_TAG));
        if (toTool != null) {
            toTool.enteredHand(player, toItem);
        }
    }


    private void swapItem(@NotNull PlayerSwapItemEvent event) {
        final Player player = event.getPlayer();

        // Exit immediately if old-main hand item is not a debug tool
        if (!event.getOffHandItem().hasTag(DEBUG_TOOL_TAG))
            return;
        event.setCancelled(true);

        final Inventory toolSelector = new Inventory(InventoryType.CHEST_2_ROW, "Tool Selector");
        toolSelector.addInventoryCondition(this::selectTool);
        REGISTRY.values().stream()
                .sorted(Comparator.comparing(DebugTool::name))
                .forEach(tool -> toolSelector.addItemStack(tool.itemStack()
                        .withTag(DEBUG_TOOL_TAG, tool.name())));
        player.openInventory(toolSelector);
    }

    private void disconnect(@NotNull PlayerDisconnectEvent event) {
        debounce.remove(event.getPlayer().getUuid());
    }

    private void selectTool(@NotNull Player player, int slot, @NotNull ClickType clickType, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        if (clickType != ClickType.LEFT_CLICK) return;

        final Inventory inventory = player.getOpenInventory();
        if (inventory == null) return;
        final ItemStack clickedItem = inventory.getItemStack(slot);
        if (clickedItem.isAir()) return;

        final ItemStack oldItem = player.getInventory().getItemStack(player.getHeldSlot());
        final DebugTool oldTool = REGISTRY.get(oldItem.getTag(DEBUG_TOOL_TAG));
        if (oldTool != null) {
            oldTool.exitedHand(player, oldItem);
        }

        player.getInventory().setItemStack(player.getHeldSlot(), clickedItem);
        final DebugTool newTool = REGISTRY.get(clickedItem.getTag(DEBUG_TOOL_TAG));
        newTool.enteredHand(player, clickedItem);

        player.closeInventory();
    }


    /**
     * Debounces clicks. This is mostly relevant to the left click handling using the hand animation packet.
     *
     * @return True if the action should be ignored
     */
    private boolean debounce(@NotNull Player player) {
        long now = System.currentTimeMillis();
        long nextClick = debounce.getOrDefault(player.getUuid(), 0L);
        if (nextClick > now) return true;
        debounce.put(player.getUuid(), 500 + now);
        return false;
    }

}
