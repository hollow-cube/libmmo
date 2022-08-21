package unnamed.mmo.entity.brain.navigator;

import net.minestom.server.entity.Entity;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

public class TestUtil {

    public static Stream<Arguments> navigators() {
        return Stream.of(
//                Arguments.of("enodia", (Function<Entity, Navigator>) EnodiaNavigator::new),
//                Arguments.of("hydrazine", (Function<Entity, Navigator>) HydrazineNavigator::new),
                Arguments.of("custom", (Function<Entity, Navigator>) CustomNavigator::new)
        );
    }
}
