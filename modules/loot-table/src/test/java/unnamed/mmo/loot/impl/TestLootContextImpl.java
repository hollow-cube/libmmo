package unnamed.mmo.loot.impl;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.junit.jupiter.api.Test;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.loot.LootContext;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TestLootContextImpl {
    private static final LootContext.Key<Vec> TEST_KEY = new LootContext.Key<>("test", Vec.class);
    private static final LootContext.Key<Point> TEST_KEY_2 = new LootContext.Key<>("test", Point.class);

    @Test
    public void testGetMissing() {
        var lootContext = new LootContextImpl(Map.of(), NumberSource.constant(1));
        var result = lootContext.get(TEST_KEY);

        assertThat(result).isNull();
    }

    @Test
    public void testGetPresent() {
        var lootContext = new LootContextImpl(Map.of("test", new Vec(1, 1, 1)), NumberSource.constant(1));
        var result = lootContext.get(TEST_KEY);

        assertThat(result).isEqualTo(new Vec(1, 1, 1));
    }

    @Test
    public void testGetSupertype() {
        var lootContext = new LootContextImpl(Map.of("test", new Vec(1, 1, 1)), NumberSource.constant(1));
        var result = lootContext.get(TEST_KEY_2);

        assertThat(result).isEqualTo(new Vec(1, 1, 1));
    }

    @Test
    public void testGetWrongType() {
        var lootContext = new LootContextImpl(Map.of("test", "wrong"), NumberSource.constant(1));
        var result = lootContext.get(TEST_KEY);

        assertThat(result).isNull();
    }
}
