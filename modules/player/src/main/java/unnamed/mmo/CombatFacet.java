package unnamed.mmo;

import com.google.auto.service.AutoService;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.damage.DamageProcessor;
import unnamed.mmo.server.Facet;
import unnamed.mmo.server.ServerWrapper;

@AutoService(Facet.class)
public class CombatFacet implements Facet {
    @Override
    public void hook(@NotNull ServerWrapper server) {
        DamageProcessor.init();
    }
}
