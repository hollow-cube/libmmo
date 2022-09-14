package net.hollowcube.util;

import net.hollowcube.Env;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EventUtil {

    /**
     * Creates an event node with the given name for any event which has not yet been cancelled.
     */
    public static EventNode<Event> notCancelledNode(@NotNull String name) {
        return EventNode.event(name, EventFilter.ALL, Predicate.not(EventUtil::isCancelled));
    }

    public static boolean isCancelled(Event event) {
        return event instanceof CancellableEvent c && c.isCancelled();
    }

    /**
     * Call an event in a test-safe manner.
     */
    public static void safeDispatch(@NotNull Event event) {
        ServerProcess process = MinecraftServer.process();
        if (process == null && !Env.STRICT_MODE)
            return;
        process.eventHandler().call(event);
    }

    /**
     * Call an event in a test-safe manner.
     */
    public static void safeDispatchCancellable(@NotNull Event event, @NotNull Runnable onSuccess) {
        ServerProcess process = MinecraftServer.process();
        if (process == null && !Env.STRICT_MODE)
            return;
        process.eventHandler().callCancellable(event, onSuccess);
    }
}
