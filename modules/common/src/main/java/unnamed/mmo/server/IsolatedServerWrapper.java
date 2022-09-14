package unnamed.mmo.server;

import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
record IsolatedServerWrapper(@NotNull ServerProcess process) implements ServerWrapper {

    @Override
    public <F extends Facet> @Nullable F getFacet(@NotNull Class<F> type) {
        return null;
    }

    @Override
    public void addEventNode(@NotNull EventNode<?> node) {
        process.eventHandler().addChild(node);
    }

    @Override
    public void registerCommand(@NotNull Command command) {
        process.command().register(command);
    }

    @Override
    public void registerBlockHandler(@NotNull Supplier<BlockHandler> handlerSupplier) {
        process.block().registerHandler(handlerSupplier.get().getNamespaceId(), handlerSupplier);
    }
}
