package unnamed.mmo.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.util.ComponentUtil;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Naive component translation system.
 * <p>
 * Should be replaced with adventure translation or something else in the future.
 */
public class LanguageProvider {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(LanguageProvider.class.getResourceAsStream("/lang/en_US.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Pattern ARG_PATTERN = Pattern.compile("\\{[0-9]+}");

    public static @NotNull Component get(@NotNull String key, @NotNull Component... args) {
        Component component = ComponentUtil.fromStringSafe((String) properties.getOrDefault(key, key));
        if (args.length != 0) {
            //todo this seems like it could be wildly slow...
            component = component.replaceText(TextReplacementConfig.builder()
                    .match(ARG_PATTERN)
                    .replacement((result, builder) -> {
                        var group = result.group();
                        int index = Integer.parseInt(group.substring(1, group.length() - 1));
                        return index < args.length ?
                                args[index] :
                                Component.text("$$" + index);
                    }).build());
        }
        return component;

    }
}
