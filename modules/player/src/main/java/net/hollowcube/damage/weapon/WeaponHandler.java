package net.hollowcube.damage.weapon;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.item.ItemComponentHandler;

public class WeaponHandler implements ItemComponentHandler<Weapon> {

    private final EventNode<Event> eventNode = EventNode.all("starlight:weapon/item_component_handler");

    public WeaponHandler() {

    }

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("starlight:weapon");
    }

    @Override
    public @NotNull Class<Weapon> componentType() {
        return Weapon.class;
    }

    @Override
    public com.mojang.serialization.@NotNull Codec<@NotNull Weapon> codec() {
        return Weapon.CODEC;
    }

    @Override
    public @Nullable EventNode<Event> eventNode() {
        return eventNode;
    }
}
