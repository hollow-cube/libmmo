package unnamed.mmo.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import unnamed.mmo.registry.Resource;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public interface Item extends Resource.Id {

    @Contract(pure = true)
    @NotNull NamespaceID namespace();

    @Contract(pure = true)
    int id();

    @Contract(pure = true)
    int stateId();

    @Unmodifiable @NotNull Map<String, String> properties();


    @Contract(pure = true)
    default @NotNull String translationKey() {
        return String.format("item.%s.%s.name", namespace().namespace(), namespace().path());
    }

    @Contract(pure = true)
    @NotNull Material material();

    @Contract(pure = true)
    int amount();

    @Contract(pure = true)
    Item withAmount(int amount);


    // ItemStack conversion

    default @NotNull ItemStack asItemStack() {
        return ItemStack.builder(material())
                .amount(amount())
                .displayName(Component.text(translationKey()).decoration(TextDecoration.ITALIC, false))
                .meta(meta -> meta.customModelData(stateId()))
                .build();
    }

    static @NotNull Item fromItemStack(@NotNull ItemStack itemStack) {
        // Makes the assumption that there will not be unhandled items in circulation
        return Objects.requireNonNull(Item.fromId(itemStack.meta().getCustomModelData()));
    }


    // Static helpers

    static @NotNull Collection<@NotNull Item> values() {
        return ItemImpl.values();
    }

    static @Nullable Item fromNamespaceId(@NotNull String namespaceId) {
        return ItemImpl.getSafe(namespaceId);
    }

    static @Nullable Item fromNamespaceId(@NotNull NamespaceID namespaceId) {
        return ItemImpl.getSafe(namespaceId.asString());
    }

    static @Nullable Item fromId(int id) {
        return ItemImpl.getId(id);
    }

    static @Nullable Item fromStateId(int stateId) {
        return ItemImpl.getState(stateId);
    }

}
