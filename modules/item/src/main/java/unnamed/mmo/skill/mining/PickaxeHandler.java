package unnamed.mmo.skill.mining;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.component.ComponentHandler;

import java.util.function.Function;

public class PickaxeHandler implements ComponentHandler<Pickaxe> {



    @Override
    public @NotNull Function<@NotNull JsonObject, @NotNull Pickaxe> factory() {
        return Pickaxe::new;
    }
}
