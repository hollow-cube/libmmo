package unnamed.mmo.test;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ComponentUtil {

    public static @NotNull String toString(@NotNull Component component) {
        if (component instanceof TranslatableComponent comp)
            return comp.key();
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
