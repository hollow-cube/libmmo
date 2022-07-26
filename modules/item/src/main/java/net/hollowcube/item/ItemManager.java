package net.hollowcube.item;

import com.google.auto.service.AutoService;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(Facet.class)
@ApiStatus.Internal
public class ItemManager implements Facet {
    private static final Logger logger = LoggerFactory.getLogger(ItemManager.class);

    @Override
    public void hook(@NotNull ServerWrapper server) {
        EventNode<Event> eventNode = EventNode.all("starlight:item/facet");
        server.addEventNode(eventNode);

        // Component handlers
        for (ItemComponentHandler<?> handler : ItemComponentRegistry.REGISTRY.values()) {

            // Register event nodes
            final var handlerEventNode = handler.eventNode();
            if (handlerEventNode != null) {
                eventNode.addChild(handlerEventNode);
            }
        }
        logger.debug("Loaded {} item components", ItemComponentRegistry.REGISTRY.size());
    }

}
