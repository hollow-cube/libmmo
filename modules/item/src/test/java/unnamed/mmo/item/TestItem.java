package unnamed.mmo.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestItem {
    // Will need some more tests here as item creation gets more complicated (eg with components editing lore)

    @Test
    public void testAsItemStack() {
        Item item = new MockItem(
                NamespaceID.from("test:item"),
                1, 2,
                Map.of(),
                Material.GUNPOWDER,
                5
        );
        ItemStack expected = ItemStack.builder(Material.GUNPOWDER)
                .amount(5)
                .meta(builder -> {
                    builder.customModelData(2);
                    builder.displayName(Component.text("item.test.item.name").decoration(TextDecoration.ITALIC, false));
                })
                .build();

        assertEquals(expected, item.asItemStack());
    }

    @Test
    public void testFromItemStack() {
        //todo(matt) relies on loaded item data, need to handle this better (eg load data just for these tests)
    }
}
