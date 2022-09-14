package unnamed.mmo.server.dev.commands;

import net.minestom.server.command.CommandManager;

public class CommandRegister {

    public static void registerCommands(CommandManager manager) {
        manager.register(new CraftCommand());
        manager.register(new ModifierCommand());
    }
}
