package unnamed.mmo.blocks.ore;

import com.google.auto.service.AutoService;
import net.minestom.server.ServerProcess;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.ore.handler.OreBlockHandler;
import unnamed.mmo.server.Facet;

@AutoService(Facet.class)
public class MiningFacet implements Facet {

    @Override
    public void hook(@NotNull ServerProcess server) {
        server.block().registerHandler(
                OreBlockHandler.instance().getNamespaceId(),
                OreBlockHandler::instance
        );
    }

}
