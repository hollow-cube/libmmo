package net.hollowcube.entity.brain.navigator;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.function.Function;

import static com.google.common.truth.Truth.assertThat;

@EnvTest
public class TestNavigatorBasicIntegration {

    @ParameterizedTest(name = "{0}")
    @MethodSource("net.hollowcube.entity.brain.navigator.TestUtil#navigators")
    public void testBasicMovement(String name, Function<Entity, Navigator> newNavigator, Env env) {
        var entity = new Entity(EntityType.ZOMBIE);
        var navigator = newNavigator.apply(entity);

        var instance = env.createFlatInstance();
        instance.loadChunk(0, 0).join();

        entity.setInstance(instance, new Pos(5, 40, 5)).join();
        navigator.setInstance(instance);

        var pathFound = navigator.setPathTo(new Pos(0, 40, 0));
        var result = env.tickWhile(() -> {
            System.out.println(entity.getPosition());
            navigator.tick(System.currentTimeMillis());
            return navigator.isActive();
        }, Duration.ofMillis(100));

        assertThat(result).isTrue();
        assertThat(entity.getPosition().distance(new Vec(0, 40, 0))).isAtMost(1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("net.hollowcube.entity.brain.navigator.TestUtil#navigators")
    public void testBasicMovementAroundBlock(String name, Function<Entity, Navigator> newNavigator, Env env) {
        var entity = new Entity(EntityType.ZOMBIE);
        var navigator = newNavigator.apply(entity);

        var instance = env.createFlatInstance();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                instance.loadChunk(x, y).join();
            }
        }

        // Block the direct path
        instance.setBlock(3, 41, 0, Block.STONE);
        instance.setBlock(3, 42, 0, Block.STONE);

        entity.setInstance(instance, new Pos(5, 40, 0)).join();
        navigator.setInstance(instance);

        navigator.setPathTo(new Pos(0, 40, 0));
        var result = env.tickWhile(() -> {
            System.out.println(entity.getPosition());
            navigator.tick(System.currentTimeMillis());
            return navigator.isActive();
        }, Duration.ofMillis(200));

        assertThat(result).isTrue();
        assertThat(entity.getPosition().distance(new Vec(0, 40, 0))).isAtMost(1);
    }
}
