package net.hollowcube.server.dev.command;

import net.hollowcube.modifiers.ModifierOperation;
import net.hollowcube.modifiers.ModifierType;
import net.hollowcube.player.PlayerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
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

import java.util.Locale;

public class ModifierCommand extends Command {
    public ModifierCommand() {
        super("modifier");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("add", "remove", "list", "listall", "removeall");
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
                        addPermanentModifier(player, context.get(modifierTypeArg), context.get(modifierIdArg), context.get(amountArg), context.get(operationArg));
                    }
                }
            } else {
                sender.sendMessage(Component.translatable("command.modifier.invalid_argument_num", Style.style(NamedTextColor.RED), Component.text("add"), Component.text(context.getRaw(modeArg))));
            }
        }, modeArg, playerArg, modifierTypeArg, modifierIdArg, amountArg, operationArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("add".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        addTempModifier(player, context.get(modifierTypeArg), context.get(modifierIdArg), context.get(amountArg), context.get(operationArg), context.get(modifierDurationArg).longValue() * MinecraftServer.TICK_MS);
                    }
                }
            } else {
                sender.sendMessage(Component.translatable("command.modifier.invalid_argument_num", Style.style(NamedTextColor.RED), Component.text("add"), Component.text(context.getRaw(modeArg))));
            }
        }, modeArg, playerArg, modifierTypeArg, modifierIdArg, amountArg, operationArg, modifierDurationArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("remove".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        removeModifier(player, context.get(modifierTypeArg), context.get(modifierIdArg));
                    }
                }
            } else {
                sender.sendMessage(Component.translatable("command.modifier.invalid_argument_num", Style.style(NamedTextColor.RED), Component.text("remove"), Component.text(context.getRaw(modeArg))));
            }
        }, modeArg, playerArg, modifierTypeArg, modifierIdArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("listall".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        listAllModifiers(sender, player);
                    }
                }
            } else {
                sender.sendMessage(Component.translatable("command.modifier.invalid_argument_num", Style.style(NamedTextColor.RED), Component.text("listall"), Component.text(context.getRaw(modeArg))));
            }
        }, modeArg, playerArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("list".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        listModifier(sender, player, context.get(modifierTypeArg));
                    }
                }
            } else {
                sender.sendMessage(Component.translatable("command.modifier.invalid_argument_num", Style.style(NamedTextColor.RED), Component.text("list"), Component.text(context.getRaw(modeArg))));
            }
        }, modeArg, playerArg, modifierTypeArg);

        addSyntax((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            if ("removeall".equals(mode)) {
                for (Entity entity : context.get(playerArg).find(sender)) {
                    if (entity instanceof PlayerImpl player) {
                        removeAllWithId(player, context.get(modifierIdArg));
                    }
                }
            } else {
                sender.sendMessage(Component.translatable("command.modifier.invalid_argument_num", Style.style(NamedTextColor.RED), Component.text("removeall"), Component.text(context.getRaw(modeArg))));
            }
        }, modeArg, playerArg, modifierIdArg);
    }

    private void listAllModifiers(CommandSender sender, PlayerImpl player) {
        if(player.getDisplayName() != null) {
            sender.sendMessage(player.getDisplayName().append(Component.text("'s Modifiers:")));
        } else {
            sender.sendMessage(Component.text(player.getUsername() + "'s Modifiers:"));
        }

        for (var entry : player.getCurrentModifierValues().entrySet()) {
            sender.sendMessage(Component.text("  " + entry.getKey() + ": ", NamedTextColor.AQUA).append(Component.text(entry.getValue(), NamedTextColor.DARK_AQUA)));
        }
    }
    private void listModifier(CommandSender sender, PlayerImpl player, String modifierType) {
        Component playerName = player.getDisplayName() != null ? player.getDisplayName() : Component.text(player.getUsername());
        double amount = player.getModifierValue(modifierType);
        sender.sendMessage(playerName.append(Component.text(" has a value of ")).append(Component.text(amount)).append(Component.text(" for modifier " + modifierType + ".")));
    }

    private void addTempModifier(PlayerImpl player, String modifierType, String modifierId, double amount, ModifierOperation operation, long tickDuration) {
        player.addTemporaryModifier(modifierType, modifierId, amount, operation, tickDuration);
    }

    private void addPermanentModifier(PlayerImpl player, String modifierType, String modifierId, double amount, ModifierOperation operation) {
        player.addPermanentModifier(modifierType, modifierId, amount, operation);
    }

    private void removeModifier(PlayerImpl player, String modifierType, String modifierId) {
        player.removeModifier(modifierType, modifierId);
    }

    private void removeAllWithId(PlayerImpl player, String modifierId) {
        player.removeAllModifiersWithId(modifierId);
    }
}