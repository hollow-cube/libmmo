package unnamed.mmo.item.component;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public record TestComponent(
        @NotNull String name
) implements ItemComponent {

    public TestComponent(JsonObject obj) {
        //todo abstraction on data loading
        this(obj.get("name").getAsString());
    }
}
