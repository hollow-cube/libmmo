package unnamed.mmo.chat.command;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionCallback;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.chat.ChatQuery;
import unnamed.mmo.chat.storage.ChatStorage;

import java.util.List;

public class LogCommand extends Command {
    private final ChatStorage storage;

    public LogCommand(@NotNull ChatStorage storage) {
        super("log");
        this.storage = storage;

        var filters = ArgumentType.Loop(
                "filters",
                ArgumentType.Group(
                        "serverGroup",
                        ArgumentType.Word("server").from("server", "on"),
                        //todo would be nice to suggest some entries? Not sure where this would come from
                        ArgumentType.Word("serverId")
                ),
                ArgumentType.Group(
                        "contextGroup",
                        ArgumentType.Word("context").from("context", "in"),
                        ArgumentType.Word("contextId")
                                //todo better way to add an optional suggestion?
                                .setSuggestionCallback((sender, context, suggestion) -> suggestion.addEntry(new SuggestionEntry("global")))
                ),
                ArgumentType.Group(
                        "fromGroup",
                        ArgumentType.Literal("from"),
                        //todo is entity only online players? Can you input a UUID?
                        ArgumentType.Entity("fromEntity")
                                .onlyPlayers(true)
                                .singleEntity(true)
                )
        );

        // log [...]
        addSyntax(this::onLog, filters);
    }

    private void onLog(CommandSender sender, CommandContext context) {

        ChatQuery.Builder query = ChatQuery.builder();
        parseFilters(query, context.get("filters"));

        storage.queryChatMessages(query.build());
    }

    private void parseFilters(ChatQuery.Builder query, List<CommandContext> filters) {
        for (CommandContext context : filters) {
            switch (context.getCommandName()) {
                case "server", "on" -> {
                    final String serverId = context.get("serverId");
                    query.serverId(serverId);
                }
                case "context", "in" -> {
                    final String contextId = context.get("contextId");
                    query.context(contextId);
                }
                default -> throw new RuntimeException("bad command");
            }
        }
    }


}
