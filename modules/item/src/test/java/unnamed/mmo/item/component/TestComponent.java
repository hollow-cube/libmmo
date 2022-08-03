package unnamed.mmo.item.component;

import com.google.gson.JsonObject;
import net.minestom.server.registry.Registry;
import org.jetbrains.annotations.NotNull;

public record TestComponent(
        @NotNull String name
) implements ItemComponent {

    public TestComponent(Registry.Properties obj) {
        //todo abstraction on data loading
        this(obj.getString("name"));
    }
}
