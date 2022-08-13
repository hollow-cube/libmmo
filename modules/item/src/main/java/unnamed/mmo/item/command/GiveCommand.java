package unnamed.mmo.item.command;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.command.arg.ArgumentItem;

import java.util.Comparator;
import java.util.List;

/**
 * Give command to replace the vanilla one, only can give custom items.
 */
public class GiveCommand extends Command {
    private final Argument<EntityFinder> targetArgument = ArgumentType.Entity("entity").onlyPlayers(true);
    private final Argument<Item> itemArgument = ArgumentType.String("item").map(ArgumentItem::parse);
    private final Argument<Integer> amountArgument = ArgumentType.Integer("amount").min(1).max(64);

    public GiveCommand() {
        super("give");

        addSyntax(this::openGiveMenu);
        addSyntax(this::giveItemDirect, targetArgument, itemArgument);
        addSyntax(this::giveItemDirect, targetArgument, itemArgument, amountArgument);
    }

    private void openGiveMenu(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Incorrect usage: /give <target> <item> [amount]");
            return;
        }

        final Inventory menu = new Inventory(InventoryType.CHEST_6_ROW, "Item List");
        //todo eventually need to page this inventory, but want a gui library first i guess
        Item.values()
                .stream()
                .sorted(Comparator.comparing(Item::name))
                .limit(54)
                .map(Item::asItemStack)
                .forEach(menu::addItemStack);
        player.openInventory(menu);
    }

    private void giveItemDirect(@NotNull CommandSender sender, @NotNull CommandContext context) {
        // Get item
        final Item item = context.get(itemArgument);
        if (item == null) {
            //todo better messages
            sender.sendMessage("invalid item");
            return;
        }
        int amount = context.getOrDefault(amountArgument, 1);
        final ItemStack itemStack = item.withAmount(amount).asItemStack();

        // Get targets
        final EntityFinder targetFinder = context.get(targetArgument);
        final List<Entity> targets = targetFinder.find(sender);

        // Distribute
        for (Entity target : targets) {
            if (target instanceof Player player) {
                player.getInventory().addItemStack(itemStack);
            }
        }

        //todo better message
        sender.sendMessage("Success!");
    }
}
