package unnamed.mmo.registry;

import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

//todo(matt): An ID container test at least, and perhaps some unhappy cases
public class TestRegistryContainer {

    @BeforeAll
    public static void setup() {
        Registry.DATA_PATH = Paths.get("src/test/resources");
    }

    @Test
    public void testCreateContainer() {
        var container = Registry.createContainer(TestResource.RESOURCE_TYPE, (namespace, props) ->
                new TestResource(NamespaceID.from(namespace), props.getString("string")));

        assertEquals(
                new TestResource(NamespaceID.from("test:one"), "hello"),
                container.get("test:one")
        );
    }

}
