package unnamed.mmo.item;

import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Test;
import unnamed.mmo.item.component.TestComponent;
import unnamed.mmo.registry.Registry;

import java.io.StringReader;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TestItemRegistry {
    static {
        System.setProperty("unnamed.data.dir", "src/test/resources");
    }

    @Test
    public void testItemComponentLoading() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("""
                {
                  "id": 0,
                  "material": "minecraft:gold_ingot",
                  "components": {
                    "test:component": {
                      "name": "Hello, world"
                    }
                  },
                  "defaultStateId": 0,
                  "states": {
                    "[]": {
                      "stateId": 0
                    }
                  }
                }"""));
        var props = Registry.readObject(reader);
        reader.close();

        Item item = ItemRegistry.LOADER.get("test:item", net.minestom.server.registry.Registry.Properties.fromMap((Map<String, Object>) props));

        TestComponent component = item.getComponent("test:component");
        assertThat(component).isNotNull();

        TestComponent component2 = item.getComponent(TestComponent.class);
        assertThat(component2).isNotNull();
    }
}
