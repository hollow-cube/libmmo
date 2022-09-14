package net.hollowcube.blocks.ore;

import com.google.auto.service.AutoService;
import net.hollowcube.blocks.ore.handler.OreBlockHandler;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;

@AutoService(Facet.class)
public class MiningFacet implements Facet {

    @Override
    public void hook(@NotNull ServerWrapper server) {
        server.registerBlockHandler(OreBlockHandler::instance);
    }

}
