package unnamed.mmo.entity.brain;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import sexy.kostya.enodia.EnodiaPF;
import sexy.kostya.enodia.movement.MovementProcessor;
import sexy.kostya.enodia.movement.importance.MovementImportance;
import sexy.kostya.enodia.pathfinding.PathfindingCapabilities;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnvTest
public class TestEnodiaPF {

    @Test
    public void test(Env env) {
        var enodia = EnodiaPF.Companion.forImmutableWorlds();
        var hub = enodia.initializeMovementProcessingHub(
                2,
                5,
                ent -> Attribute.MOVEMENT_SPEED.defaultValue(),
                (a, b, c) -> true
        );

        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        var capabilities = PathfindingCapabilities.Companion.getDefault();
        var entity = new EnodiaEntity();
        entity.setInstance(instance, new Pos(5, 42, 5))
                .thenAccept(unused -> {
                    entity.movementProcessor = hub.createMovementProcessor(entity, capabilities);

                    entity.movementProcessor.goTo(player, MovementImportance.Companion.getUNIMPORTANT(), 2);
                })
                .join();

        boolean result = env.tickWhile(() -> {
            System.out.println(entity.getPosition());
            return !entity.getPosition().sameBlock(new Vec(0, 40, 0));
        }, Duration.ofMillis(100));

        assertTrue(result);

    }

    static class EnodiaEntity extends Entity {

        public EnodiaEntity() {
            super(EntityType.ZOMBIE);
        }

        MovementProcessor movementProcessor = null;

        @Override
        public void update(long time) {
            super.update(time);
            if (movementProcessor != null) {
                movementProcessor.tick(time);
            }
        }
    }
}
