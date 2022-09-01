package unnamed.mmo.entity.brain.stimuli;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.entity.brain.Brain;
import unnamed.mmo.entity.brain.SingleTaskBrain;

import java.util.ArrayList;
import java.util.List;

public class NearbyEntityStimuliSource implements StimuliSource {

    @Override
    public void update(@NotNull Brain brain) {
        final Entity entity = brain.entity();
        final Instance instance = entity.getInstance();

        List<Player> nearby = new ArrayList<>();
        instance.getEntityTracker().nearbyEntities(entity.getPosition(), 5, EntityTracker.Target.PLAYERS, nearby::add);

        double minDistance = Double.MAX_VALUE;
        Player closest = null;
        for (Player player : nearby) {
            double distance = player.getDistanceSquared(entity);
            if (distance < minDistance) {
                minDistance = distance;
                closest = player;
            }
        }

        ((SingleTaskBrain) brain).setTarget(closest);
    }

}
