package unnamed.mmo.server;

import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public interface ServerWrapper {

    static @NotNull ServerWrapper isolated(@NotNull ServerProcess process) {
        return new IsolatedServerWrapper(process);
    }

    /**
     * Access to the underlying server process. Should not be used if there is an alternative api present in this
     * class.
     */
    @NotNull ServerProcess process();

    /**
     * Fetches a {@link Facet} loaded on the server.
     * <p>
     * Note: Load order is _not_ guaranteed. If this method is accessed during the facet loading phase of server start,
     * the target may not have been loaded yet. It will still be returned in this case.
     *
     * @return The facet if it is present on the server, otherwise null
     */
    <F extends Facet> @Nullable F getFacet(@NotNull Class<F> type);


    // Utility functions

    void addEventNode(@NotNull EventNode<?> node);

    void registerCommand(@NotNull Command command);

    void registerBlockHandler(@NotNull Supplier<BlockHandler> handlerSupplier);

}
