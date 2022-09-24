package net.hollowcube.entity;

import net.hollowcube.entity.brain.Brain;
import net.hollowcube.entity.brain.SingleTaskBrain;
import net.hollowcube.entity.brain.task.Task;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.other.SlimeMeta;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UnnamedEntity extends LivingEntity {
    private final Brain brain;

    public UnnamedEntity(Task task) {
        super(EntityType.ZOMBIE);
        if (getEntityMeta() instanceof SlimeMeta slimeMeta) {
            slimeMeta.setSize(1);
        }
        brain = new SingleTaskBrain(this, task);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition).thenAccept(unused -> {
            brain.setInstance(instance);
        });
    }

    @Override
    public void update(long time) {
        super.update(time);

        if (instance != null) {
            brain.tick(time);
        }

    }

    public @NotNull Brain brain() {
        return brain;
    }

    public void attack(@NotNull Entity target) {
        swingMainHand();
        EntityAttackEvent attackEvent = new EntityAttackEvent(this, target);
        EventDispatcher.call(attackEvent);
    }
}