package net.hollowcube.command;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.Resource;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public final class ExtraArguments {
    private ExtraArguments() {}

    /**
     * Returns an argument that targets a Resource within a Registry. The argument will suggest the resource names,
     * including just the path.
     * <p>
     * The namespace AND id must be provided to match.
     */
    public static <T extends Resource> @NotNull Argument<T> Resource(@NotNull Registry<T> registry, @NotNull String id) {
        final var ids = registry.keys().stream().map(NamespaceID::from).toList();
        System.out.println("Create tool with ids: " + ids);
        return ArgumentType.ResourceLocation(id)
                .setSuggestionCallback((sender, context, suggestions) -> {
                    String input = suggestions.getInput().substring(suggestions.getStart() - suggestions.getLength()).trim();

                    System.out.println("arg "+ input);

                    var i = 0;
                    for (var namespace : ids) {
                        if (i++ > 30) break; // Do not send too many suggestions
                        if (namespace.asString().startsWith(input) || namespace.path().startsWith(input)) {
                            suggestions.addEntry(new SuggestionEntry(namespace.asString()));
                        }
                    }
                })
                .map(s -> {
                    var value = registry.get(s);
                    if (value == null) throw new ArgumentSyntaxException("Unknown resource: " + s, s, 0);
                    return value;
                });
    }
}
