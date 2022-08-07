package unnamed.mmo.item.v2;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;
import unnamed.mmo.item.ItemRegistry;

public class TestItemRegistry {

    @Test
    public void testEntryV2Codec() {
        JsonElement json = JsonParser.parseString("""
                {
                    "namespace": "test:item",
                    "id": 1,
                    "stateId": 1,
                    "material": "minecraft:blaze_rod",
                    "components": {
                        "test:component": "abx"
                    }
                }""");

        var result = JsonOps.INSTANCE
                .withDecoder(ItemRegistry.EntryV2.CODEC)
                .apply(json)
                .getOrThrow(false, ignored -> {});

        System.out.println(result);
    }
}
