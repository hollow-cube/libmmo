package unnamed.mmo.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentItemStack;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveCommand extends Command {

    public GiveCommand() {
        super("give");

        ArgumentItemStack itemArg = ArgumentType.ItemStack("item");
        ArgumentEntity playerArg = ArgumentType.Entity("players").onlyPlayers(true);

        addSyntax((sender, context) -> {
            if(sender instanceof Player player) {
                player.getInventory().addItemStack(context.get(itemArg));
            }
        }, itemArg);

        addSyntax((sender, context) -> {
            ItemStack stack = context.get(itemArg);
            for(Entity entity : context.get(playerArg).find(sender)) {
                if(entity instanceof Player player) {
                    player.getInventory().addItemStack(stack);
                }
            }
        }, itemArg, playerArg);
    }
}
