package unnamed.mmo.item;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;
import unnamed.mmo.item.test.MockItem;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
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
                    builder.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES);
                    builder.customModelData(2);
                    builder.displayName(Component.text("item.test.item.name"));
                })
                .build();

        assertEquals(expected, item.asItemStack());
    }

    @Test
    public void testFromItemStack() {
        var itemStack = ItemStack.of(Material.GOLD_INGOT)
                .withMeta(meta -> meta.customModelData(1));

        var item = Item.fromItemStack(itemStack);

        var expected = Item.fromNamespaceId("test:item");
        assertThat(item).isEqualTo(expected);
    }
}
