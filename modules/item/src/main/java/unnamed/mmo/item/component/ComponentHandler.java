package unnamed.mmo.item.component;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ComponentHandler<C extends ItemComponent> {

    @NotNull Function<@NotNull JsonObject, @NotNull C> factory();

}
