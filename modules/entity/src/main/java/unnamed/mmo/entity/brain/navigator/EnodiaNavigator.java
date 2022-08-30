package unnamed.mmo.entity.brain.navigator;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.Instance;
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

    private final Entity entity;
    private MovementProcessor enodia;

    EnodiaNavigator(@NotNull Entity entity) {
        this.entity = entity;
        this.enodia = null;
    }

    @Override
    public void setInstance(@NotNull Instance instance) {
        enodia = MOVEMENT_HUB.createMovementProcessor(entity, DEFAULT_CAPABILITIES);
    }

    @Override
    public boolean setPathTo(@Nullable Point point) {
        if (point == null) {
            this.enodia.stop(true);
            return true;
        }

        // Last parameter here is the max distance from the value.
        return enodia.goTo(point, DEFAULT_IMPORTANCE, 0.5f);
    }

    @Override
    public boolean isActive() {
        if (enodia == null) {
            return false;
        }
        return enodia.isActive();
    }

    @Override
    public void tick(long time) {
        if (enodia != null) {
            enodia.tick(time);
        }
    }
}
