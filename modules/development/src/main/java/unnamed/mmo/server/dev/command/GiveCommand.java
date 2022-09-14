package unnamed.mmo.server.dev.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentItemStack;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public class GiveCommand extends Command {

    public GiveCommand() {
        super("give");

        ArgumentItemStack itemArg = ArgumentType.ItemStack("item");
        ArgumentInteger amountArg = ArgumentType.Integer("amount");
        ArgumentEntity playerArg = ArgumentType.Entity("players").onlyPlayers(true);

        amountArg.setDefaultValue(1);
        amountArg.between(1, 64);

        addSyntax((sender, context) -> {
            int amount = context.get(amountArg);
            if (sender instanceof Player player) {
                player.getInventory().addItemStack(context.get(itemArg).withAmount(amount));
            }
        }, itemArg, amountArg);

        addSyntax((sender, context) -> {
            int amount = context.get(amountArg);
            ItemStack stack = context.get(itemArg).withAmount(amount);
            for (Entity entity : context.get(playerArg).find(sender)) {
                if (entity instanceof Player player) {
                    player.getInventory().addItemStack(stack);
                }
            }
        }, itemArg, playerArg, amountArg);
    }
}
