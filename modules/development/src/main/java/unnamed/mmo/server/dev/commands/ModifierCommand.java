package unnamed.mmo.server.dev.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import unnamed.mmo.modifiers.ModifierOperation;
import unnamed.mmo.modifiers.ModifierType;
import unnamed.mmo.player.PlayerImpl;

import java.util.Locale;

public class ModifierCommand extends Command {
    public ModifierCommand() {
        super("modifier");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("add", "remove");
        ArgumentInteger modifierDurationArg = ArgumentType.Integer("durationType");
        ArgumentEntity playerArg = ArgumentType.Entity("players").onlyPlayers(true);
        ArgumentString modifierTypeArg = ArgumentType.String("modifierType");
        ArgumentString modifierIdArg = ArgumentType.String("modifierId");
        ArgumentDouble amountArg = ArgumentType.Double("amount");
        ArgumentEnum<ModifierOperation> operationArg = ArgumentType.Enum("operation", ModifierOperation.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        modifierTypeArg.setSuggestionCallback((sender, context, suggestion) -> {
            for (ModifierType modifierType : ModifierType.REGISTRY.values()) {
                suggestion.addEntry(new SuggestionEntry(modifierType.namespace().toString()));
            }
        });

        modifierDurationArg.min(0);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("add".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        player.addPermanentModifier(context.get(modifierTypeArg), context.get(modifierIdArg), context.get(amountArg), context.get(operationArg));
                    }
                }
            } else {
                // TODO: Make translation
                sender.sendMessage(Component.text("Invalid mode for the amount of arguments supplied (tried the add mode, got " + context.getRaw(mode) + " instead)", NamedTextColor.RED));
            }
        }, modeArg, playerArg, modifierTypeArg, modifierIdArg, amountArg, operationArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("add".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        player.addTemporaryModifier(context.get(modifierTypeArg), context.get(modifierIdArg), context.get(amountArg), context.get(operationArg), context.get(modifierDurationArg).longValue() * MinecraftServer.TICK_MS);
                    }
                }
            } else {
                // TODO: Make translation
                sender.sendMessage(Component.text("Invalid mode for the amount of arguments supplied (tried the add mode, got " + context.getRaw(mode) + " instead)", NamedTextColor.RED));
            }
        }, modeArg, playerArg, modifierTypeArg, modifierIdArg, amountArg, operationArg, modifierDurationArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("remove".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        player.removeModifier(context.get(modifierTypeArg), context.get(modifierIdArg));
                    }
                }
            } else {
                // TODO: Make translation
                sender.sendMessage(Component.text("Invalid mode for the amount of arguments supplied (tried the remove mode, got " + context.getRaw(mode) + " instead)", NamedTextColor.RED));
            }
        }, modeArg, playerArg, modifierTypeArg, modifierIdArg);
    }
}
