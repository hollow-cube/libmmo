package net.hollowcube.entity.brain.navigator;

import net.hollowcube.entity.navigator.Navigator;
import net.minestom.server.entity.Entity;
import org.junit.jupiter.params.provider.Arguments;
import net.hollowcube.entity.motion.MotionNavigator;

import java.util.function.Function;
import java.util.stream.Stream;

public class TestUtil {

    public static Stream<Arguments> navigators() {
        return Stream.of(
//                Arguments.of("hydrazine", (Function<Entity, Navigator>) HydrazineNavigator::new),
//                Arguments.of("custom", (Function<Entity, Navigator>) CustomNavigator::new),
                Arguments.of("motion", (Function<Entity, Navigator>) MotionNavigator::new)
        );
    }
}
