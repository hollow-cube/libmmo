package unnamed.mmo.item.loot;

import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.item.Item;
import unnamed.mmo.loot.LootContext;
import unnamed.mmo.loot.LootEntry;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestItemEntry {

    @Test
    public void testGenerateItem() {
        var entry = new ItemEntry(NamespaceID.from("test:item"));
        var context = LootContext.builder("test")
                .numbers(NumberSource.constant(1))
                .build();

        var result = entry.generate(context);
        var expected = new LootEntry.Option<>(List.of(Item.fromNamespaceId("test:item")), 1);

        assertThat(result).containsExactly(expected);
    }

    //todo codec tests
}
