package net.hollowcube.entity.stimuli;

import net.hollowcube.entity.SmartEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NearbyEntityStimuliSource implements StimuliSource {

    @Override
    public void update(@NotNull SmartEntity entity) {
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

        entity.setTarget(closest);
    }

}
