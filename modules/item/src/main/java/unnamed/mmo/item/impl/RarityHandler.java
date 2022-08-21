package unnamed.mmo.item.impl;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.ItemComponentHandler;
import unnamed.mmo.util.ComponentUtil;

@AutoService(ItemComponentHandler.class)
public class RarityHandler implements ItemComponentHandler<Rarity> {

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:rarity");
    }

    @Override
    public @NotNull Class<Rarity> componentType() {
        return Rarity.class;
    }

    @Override
    public @NotNull Codec<@NotNull Rarity> codec() {
        return Rarity.CODEC;
    }

    @Override
    public int priority() {
        return -1000;
    }

    @Override
    public void buildItemStack(@NotNull Rarity component, @NotNull ItemStack.Builder builder) {
        builder.lore(ComponentUtil.fromStringSafe(component.value()));
    }
}
