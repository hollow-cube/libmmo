package unnamed.mmo.item.component;

import com.google.gson.JsonObject;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;

import java.util.function.Function;

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





    @Override
    public @NotNull Function<@NotNull JsonObject, @NotNull TestComponent> factory() {


        return TestComponent::new;
    }

}
