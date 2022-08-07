package unnamed.mmo.item.component;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;

import java.util.function.Function;

import static net.minestom.server.registry.Registry.Properties;

@AutoService(ComponentHandler.class)
public class TestComponentHandler implements ComponentHandler<TestComponent> {
    public static <C extends TestComponent> EventFilter<PlayerEvent, C> itemComponent(Class<C> type) {
        return EventFilter.from(PlayerEvent.class, type, event -> {
            final Player player = event.getPlayer();

            // Find the target item based on the event type
            final ItemStack itemStack;
            if (event instanceof ItemEvent itemEvent)
                itemStack = itemEvent.getItemStack();
            else return null;

            Item item = Item.fromItemStack(itemStack);
            return item.getComponent(type);
        });
    }


    private final EventNode<Event> eventNode = EventNode.all("abc");

    @Override
    public @NotNull EventNode<Event> eventNode() {
        return eventNode;
    }

    @Override
    public @NotNull Class<TestComponent> componentType() {
        return TestComponent.class;
    }

    @Override
    public @NotNull Function<@NotNull Properties, @NotNull TestComponent> factory() {
        return TestComponent::new;
    }

    @Override
    public @NotNull Codec<@NotNull TestComponent> codec() {
        return TestComponent.CODEC;
    }

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("test:component");
    }
}
