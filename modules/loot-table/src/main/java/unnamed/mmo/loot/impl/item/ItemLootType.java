package unnamed.mmo.loot.impl.item;

import com.google.auto.service.AutoService;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;
import unnamed.mmo.loot.type.LootType;

@AutoService(LootType.class)
public class ItemLootType implements LootType<Item> {

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:item");
    }


}
