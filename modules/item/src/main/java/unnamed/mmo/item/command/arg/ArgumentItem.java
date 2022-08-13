package unnamed.mmo.item.command.arg;

import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.utils.block.BlockUtils;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;

/**
 * Essentially a copy of {@link net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState}
 * for now. Eventually it will also contain completion data
 * <T>
 * todo wanted to make this an argument type but cannot :(
 */
public class ArgumentItem {

    public static final int NO_ITEM = 1;
    public static final int INVALID_ITEM = 2;
    public static final int INVALID_PROPERTY = 3;
    public static final int INVALID_PROPERTY_VALUE = 4;



    public static @NotNull Item parse(@NotNull String input) throws ArgumentSyntaxException {
        final int nbtIndex = input.indexOf("[");
        if (nbtIndex == 0)
            throw new ArgumentSyntaxException("No item type", input, NO_ITEM);

        if (nbtIndex == -1) {
            // Only block name
            final Item item = Item.fromNamespaceId(input);
            if (item == null)
                throw new ArgumentSyntaxException("Invalid item type", input, INVALID_ITEM);
            return item;
        } else {
            if (!input.endsWith("]"))
                throw new ArgumentSyntaxException("Property list need to end with ]", input, INVALID_PROPERTY);
            // Block state
            final String itemName = input.substring(0, nbtIndex);
            Item item = Item.fromNamespaceId(itemName);
            if (item == null)
                throw new ArgumentSyntaxException("Invalid item type", input, INVALID_ITEM);

            // Compute properties
            final String query = input.substring(nbtIndex);
            final var propertyMap = BlockUtils.parseProperties(query);
            try {
                return item.withProperties(propertyMap);
            } catch (IllegalArgumentException e) {
                throw new ArgumentSyntaxException("Invalid property values", input, INVALID_PROPERTY_VALUE);
            }
        }
    }


}
