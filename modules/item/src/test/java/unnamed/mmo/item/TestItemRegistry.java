package unnamed.mmo.item;

import org.junit.jupiter.api.Test;
import unnamed.mmo.item.test.TestComponent;

import static com.google.common.truth.Truth.assertThat;

public class TestItemRegistry {
    static {
        System.setProperty("unnamed.data.dir", "src/test/resources");
    }

    @Test
    public void testItemComponentLoading() {
        Item item = Item.fromNamespaceId("test:item_with_component");
        assertThat(item).isNotNull();

        TestComponent component = item.getComponent("test:component");
        assertThat(component).isNotNull();

        TestComponent component2 = item.getComponent(TestComponent.class);
        assertThat(component2).isNotNull();
    }
}
