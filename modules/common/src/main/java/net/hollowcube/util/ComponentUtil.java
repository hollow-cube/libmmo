package net.hollowcube.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class ComponentUtil {

    public static @NotNull Component fromStringSafe(@NotNull String content) {
        Component mm = MiniMessage.miniMessage().deserialize(content);
        return Component.text().decoration(TextDecoration.ITALIC, false).append(mm).asComponent();
    }

}
