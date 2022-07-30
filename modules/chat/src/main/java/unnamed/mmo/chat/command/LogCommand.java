package unnamed.mmo.chat.command;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.List;

public class LogCommand extends Command {

    public LogCommand() {
        super("log");

        // log all commands /whitelist
        // log all chat stink
        // log sethprg chat poop
        // log sethprg commands
        // log sethprg all context=private[notmattw]
        // log sethprg all context=global, context=local
        // log sethprg commands /msg, /r
        // log sethprg chat 10/10/2022 server=mapmaker
        // log sethprg chat server=mapmaker_AB1C, mapmaker_235A
        var filters = ArgumentType.Loop(
                "filters",
                ArgumentType.Group(
                        "contextGroup",
                        ArgumentType.Literal("context"),
                        ArgumentType.Word("contextId")
                                .from("global")
                ),
                ArgumentType.Group(
                        "testGroup",
                        ArgumentType.Literal("test"),
                        ArgumentType.String("testStr")
                )
        );

        // log notmattw [...]
        addSyntax(this::onLog, ArgumentType.Entity("target").onlyPlayers(true).singleEntity(true), filters);

    }

    private void onLog(CommandSender sender, CommandContext context) {
        List<CommandContext> filters = context.get("filters");

    }




}
