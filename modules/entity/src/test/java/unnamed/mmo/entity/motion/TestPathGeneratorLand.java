package unnamed.mmo.entity.motion;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;
import unnamed.mmo.test.MockBlockGetter;

import static com.google.common.truth.Truth.assertThat;

public class TestPathGeneratorLand {

    @Test
    public void testSingleWaterBlock() {
        var bb = new BoundingBox(0.1, 0.1, 0.1);
        var world = MockBlockGetter.block(0, 0, 0, Block.WATER);
        var start = new Vec(0, 0, 0);

        var result = PathGenerator.WATER.generate(world, start, bb);
        assertThat(result).isEmpty();
    }

}
