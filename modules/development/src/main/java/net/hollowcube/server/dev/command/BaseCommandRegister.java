package net.hollowcube.server.dev.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;

public class BaseCommandRegister {

    public static void registerCommands() {
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new StopCommand());
        commandManager.register(new GameModeCommand());
        commandManager.register(new GiveCommand());
        commandManager.register(new CraftCommand());
        commandManager.register(new ModifierCommand());
    }
}
