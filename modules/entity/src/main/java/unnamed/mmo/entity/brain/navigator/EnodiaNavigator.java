package unnamed.mmo.entity.brain.navigator;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sexy.kostya.enodia.EnodiaPF;
import sexy.kostya.enodia.movement.MovementProcessingHub;
import sexy.kostya.enodia.movement.MovementProcessor;
import sexy.kostya.enodia.movement.importance.MovementImportance;
import sexy.kostya.enodia.pathfinding.PathfindingCapabilities;
import unnamed.mmo.entity.brain.Brain;

final class EnodiaNavigator implements Navigator {
    private static final EnodiaPF INSTANCE = EnodiaPF.Companion.forImmutableWorlds();
    private static final MovementProcessingHub MOVEMENT_HUB = INSTANCE.initializeMovementProcessingHub(
            2, 5,
            ent -> ent instanceof LivingEntity ?
                    ((LivingEntity) ent).getAttributeValue(Attribute.MOVEMENT_SPEED) :
                    Attribute.MOVEMENT_SPEED.defaultValue(),
            (unused1, unused2, unused3) -> true
    );

    private static final PathfindingCapabilities DEFAULT_CAPABILITIES = PathfindingCapabilities.Companion.getDefault();
    private static final MovementImportance DEFAULT_IMPORTANCE = MovementImportance.Companion.getUNIMPORTANT();

    private final MovementProcessor enodia;

    EnodiaNavigator(@NotNull Brain brain) {
        //todo other capabilities
        this.enodia = MOVEMENT_HUB.createMovementProcessor(brain.entity(), DEFAULT_CAPABILITIES);
    }

    @Override
    public boolean setPathTo(@Nullable Point point) {
        if (point == null) {
            this.enodia.stop(true);
            return true;
        }

        //todo what is the last parameter here?
        return enodia.goTo(point, DEFAULT_IMPORTANCE, 2);
    }

}
