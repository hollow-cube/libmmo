package unnamed.mmo.server.dev.tool;

import com.google.auto.service.AutoService;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.util.ComponentUtil;

@AutoService(DebugTool.class)
public class HelloDebugTool implements DebugTool {

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:hello");
    }

    @Override
    public @NotNull ItemStack itemStack() {
        return ItemStack.builder(Material.STICK)
                .displayName(ComponentUtil.fromStringSafe("Hello Debug Tool"))
                .lore(ComponentUtil.fromStringSafe("Capabilities:\na\nb\nc\nd"))
                .build();
    }

    @Override
    public void enteredHand(@NotNull Player player, @NotNull ItemStack itemStack) {
        player.sendMessage("HDT: Entered hand");
    }

    @Override
    public void exitedHand(@NotNull Player player, @NotNull ItemStack itemStack) {
        player.sendMessage("HDT: Exited hand");
    }

    @Override
    public @NotNull ItemStack leftClicked(@NotNull Player player, @NotNull ItemStack itemStack, @Nullable Point targetBlock, @Nullable Entity targetEntity) {
        player.sendMessage("HDT: Left clicked");
        return itemStack;
    }

    @Override
    public @NotNull ItemStack rightClicked(@NotNull Player player, @NotNull ItemStack itemStack, @Nullable Point targetBlock, @Nullable Entity targetEntity) {
        player.sendMessage("HDT: Right clicked");
        return itemStack;
    }
}
