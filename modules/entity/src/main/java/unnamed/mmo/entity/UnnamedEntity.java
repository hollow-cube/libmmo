package unnamed.mmo.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.entity.brain.SingleTaskBrain;
import unnamed.mmo.entity.brain.task.Task;
import unnamed.mmo.entity.brain.task.WanderInRegionTask;

import java.util.concurrent.CompletableFuture;

public class UnnamedEntity extends Entity {
    private final Brain brain;

    public UnnamedEntity(Task task) {
        super(EntityType.ZOMBIE);
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
}