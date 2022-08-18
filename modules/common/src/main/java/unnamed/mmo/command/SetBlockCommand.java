package unnamed.mmo.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class SetBlockCommand extends Command {

    public SetBlockCommand() {
        super("setblock");

        var positionArg = ArgumentType.RelativeBlockPosition("position");
        var blockArg = ArgumentType.BlockState("block");

        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var position = context.get(positionArg).from(player);
                var block = context.get(blockArg);

                player.getInstance().setBlock(position, block);
            }
        }, positionArg, blockArg);
    }
}
