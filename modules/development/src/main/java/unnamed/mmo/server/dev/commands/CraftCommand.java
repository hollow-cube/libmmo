package unnamed.mmo.server.dev.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.entity.Player;
import unnamed.mmo.item.crafting.CraftingInventory;
import unnamed.mmo.item.crafting.RecipeList;
import unnamed.mmo.item.crafting.ToolCraftingInventory;

import java.util.Locale;

public class CraftCommand extends Command {
    public CraftCommand() {
        super("craft");

        ArgumentWord typeArg = ArgumentType.Word("type").from("normal", "tool");

        addSyntax((sender, context) -> {
            if(sender instanceof Player player) {
                String type = context.get(typeArg).toLowerCase(Locale.ROOT);
                switch (type) {
                    case "normal" -> player.openInventory(new CraftingInventory(new RecipeList()));
                    case "tool" -> player.openInventory(new ToolCraftingInventory(new RecipeList()));
                }
            } else {
                sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            }
        }, typeArg);
    }
}
