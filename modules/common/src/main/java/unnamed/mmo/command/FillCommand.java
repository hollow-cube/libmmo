package unnamed.mmo.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class FillCommand extends Command {

    public FillCommand() {
        super("fill");

        var fromArg = ArgumentType.RelativeBlockPosition("from");
        var toArg = ArgumentType.RelativeBlockPosition("to");
        var blockArg = ArgumentType.BlockState("block");

        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var from = context.get(fromArg).from(player);
                var to = context.get(toArg).from(player);
                var block = context.get(blockArg);

                // Set all blocks in the range [from, to]
                for (int x = (int) from.x(); x <= to.x(); x++) {
                    for (int y = (int) from.y(); y <= to.y(); y++) {
                        for (int z = (int) from.z(); z <= to.z(); z++) {
                            player.getInstance().setBlock(x, y, z, block);
                        }
                    }
                }
            }
        }, fromArg, toArg, blockArg);
    }

}
