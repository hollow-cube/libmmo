package unnamed.mmo.server;

import net.minestom.server.ServerProcess;
import org.jetbrains.annotations.NotNull;

/**
 * A concept for managing server features between modules.
 * <p>
 * The idea being that the core server will load all facets using SPI, then call
 * their hook method with the starting {@link ServerProcess}.
 * <p>
 * Would need to be expanded to support references to other facets (probably wrapping
 * {@link ServerProcess} with some way to get another facet by its class). This can
 * work as long as they are all registered before {@link #hook(ServerProcess)} is called,
 * and all facets abide by a rule to never use another facet during {@link #hook(ServerProcess)}.
 * Could add a postHook or something method if absolutely necessary.
 */
public interface Facet {

    void hook(@NotNull ServerProcess server);

}
