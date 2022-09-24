package net.hollowcube.test;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class MockBlockGetter implements Block.Getter {
    private final Map<Point, Block> data;

    public static MockBlockGetter empty() {
        return new MockBlockGetter(new HashMap<>());
    }

    public static MockBlockGetter block(int x, int y, int z, Block block) {
        Map<Point, Block> data = new HashMap<>();
        data.put(new Vec(x, y, z), block);
        return new MockBlockGetter(data);
    }

    public static MockBlockGetter range(int startX, int startY, int startZ, int endX, int endY, int endZ, Block block) {
        Map<Point, Block> data = new HashMap<>();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    data.put(new Vec(x, y, z), block);
                }
            }
        }
        return new MockBlockGetter(data);
    }

    public MockBlockGetter(Map<Point, Block> data) {
        this.data = data;
    }

    @Override
    public @UnknownNullability Block getBlock(int x, int y, int z, @NotNull Condition condition) {
        return data.getOrDefault(new Vec(x, y, z), Block.AIR);
    }

    public MockBlockGetter set(int x, int y, int z, Block block) {
        data.put(new Vec(x, y, z), block);
        return this;
    }

}
