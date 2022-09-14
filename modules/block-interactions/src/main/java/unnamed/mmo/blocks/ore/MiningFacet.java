package unnamed.mmo.blocks.ore;

import com.google.auto.service.AutoService;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.ore.handler.OreBlockHandler;
import unnamed.mmo.server.Facet;
import unnamed.mmo.server.ServerWrapper;

@AutoService(Facet.class)
public class MiningFacet implements Facet {

    @Override
    public void hook(@NotNull ServerWrapper server) {
        server.registerBlockHandler(OreBlockHandler::instance);
    }

}
