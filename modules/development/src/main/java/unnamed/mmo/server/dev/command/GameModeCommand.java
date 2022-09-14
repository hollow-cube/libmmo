package unnamed.mmo.server.dev.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GameModeCommand extends Command {
    public GameModeCommand() {
        super("gamemode", "gm");

        ArgumentEnum<GameMode> modeArg = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentEntity playerArg = ArgumentType.Entity("players").onlyPlayers(true);

        addSyntax((sender, context) -> {
            if(sender instanceof Player player) {
                player.setGameMode(context.get(modeArg));
            }
        }, modeArg);

        addSyntax((sender, context) -> {
            GameMode mode = context.get(modeArg);
            for(Entity entity : context.get(playerArg).find(sender)) {
                if(entity instanceof Player player) {
                    player.setGameMode(mode);
                }
            }
        }, modeArg, playerArg);
    }
}
