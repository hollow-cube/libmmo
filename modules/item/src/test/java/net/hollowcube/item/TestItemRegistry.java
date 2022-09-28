package net.hollowcube.item;

import net.hollowcube.item.test.TestComponent;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestItemRegistry {
    static {
        System.setProperty("starlight.data.dir", "src/test/resources");
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
