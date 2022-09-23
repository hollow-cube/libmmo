package net.hollowcube.item.impl;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.hollowcube.util.ComponentUtil;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.item.ItemComponentHandler;

@AutoService(ItemComponentHandler.class)
public class RarityHandler implements ItemComponentHandler<Rarity> {

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("starlight:rarity");
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
