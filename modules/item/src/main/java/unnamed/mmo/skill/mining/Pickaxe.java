package unnamed.mmo.skill.mining;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.component.ItemComponent;

public record Pickaxe(
        int miningSpeed
) implements ItemComponent {

    public Pickaxe(@NotNull JsonElement json) {
        this(json.getAsJsonObject().get("miningSpeed").getAsInt());
    }
}
