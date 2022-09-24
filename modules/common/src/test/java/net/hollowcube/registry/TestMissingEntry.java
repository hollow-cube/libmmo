package net.hollowcube.registry;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestMissingEntry {

    @Test
    public void testMissingRegistryEntry() {
        var json = JsonParser.parseString("{\"type\": \"missing\"}");
        assertThrows(MissingEntryException.class, () -> JsonOps.INSTANCE.withDecoder(TestResource.CODEC).apply(json));
    }

}
