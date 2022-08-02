package unnamed.mmo.skill.mining;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.component.ComponentHandler;
import unnamed.mmo.player.event.PlayerLongDiggingStartEvent;
import unnamed.mmo.player.event.PlayerLongDiggingTickEvent;

import java.util.function.Function;

public class PickaxeHandler implements ComponentHandler<Pickaxe> {


    private void handleLongBreakStart(PlayerLongDiggingStartEvent event) {
        final Ore ore = event.getBlock().getTag(Ore.TAG);
        if (ore == null) return;

        //todo ensure they are using a pickaxe and that the pickaxe meets min specs
        // might be handled by eventNode in item component handler that only passes events with the item.

        event.setDiggingBlock("ignored", ore.health());
    }

    private void handleLongBreakTick(PlayerLongDiggingTickEvent event) {
        //todo get pickaxe mining speed

        //todo crit chance would be done here

        event.setDamage(1);
    }


    @Override
    public @NotNull Function<@NotNull JsonObject, @NotNull Pickaxe> factory() {
        return Pickaxe::new;
    }
}
