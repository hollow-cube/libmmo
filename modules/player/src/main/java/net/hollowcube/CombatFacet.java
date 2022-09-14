package net.hollowcube;

import com.google.auto.service.AutoService;
import net.hollowcube.damage.DamageProcessor;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;

@AutoService(Facet.class)
public class CombatFacet implements Facet {
    @Override
    public void hook(@NotNull ServerWrapper server) {
        DamageProcessor.init();
    }
}
