package unnamed.mmo.server;

import net.minestom.server.ServerProcess;
import org.jetbrains.annotations.NotNull;

/**
 * A concept for loading modules of the server, similar-ish to extensions.
 * <p>
 * Facets are defined, then loaded using the service provider interface by
 * the controlling server (development, production, etc).
 * <p>
 * All facets <i>must</i> have a public no-args constructor. There may be
 * other constructors present (eg for use in tests).
 * <p>
 * Common registration functions are provided on {@link ServerWrapper}.
 * These should be used over accessing the {@link ServerProcess} directly,
 * because they can be transparently extended to support unloading facets
 * if this ever becomes a desired behavior (and they have some utilities).
 */
public interface Facet {

    void hook(@NotNull ServerWrapper server);

}
