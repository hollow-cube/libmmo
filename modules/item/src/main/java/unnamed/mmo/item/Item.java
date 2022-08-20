package unnamed.mmo.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.attribute.ItemAttribute;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import unnamed.mmo.lang.LanguageProvider;
import unnamed.mmo.registry.Resource;

import java.util.*;
import java.util.stream.Stream;

public interface Item extends Resource.Id {

    Codec<Item> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(Item::name),
            Codec.INT.fieldOf("amount").forGetter(Item::amount)
    ).apply(i, (name, amount) -> Objects.requireNonNull(Item.fromNamespaceId(name))
            //todo better error if null item? not sure how we should handle deserializing a bad item.
            .withAmount(amount)));

    @Override
    @Contract(pure = true)
    @NotNull NamespaceID namespace();

    @Override
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


    // Components

    /**
     * Returns all of the {@link ItemComponent}s on this item.
     */
    @NotNull Stream<ItemComponent> components();

    default <C extends ItemComponent> @Nullable C getComponent(Class<C> type) {
        return getComponent(ItemComponentHandler.from(type).name());
    }

    @SuppressWarnings("TypeParameterUnusedInFormals")
    <C extends ItemComponent> @Nullable C getComponent(@NotNull String namespace);
    //todo might want to consider removing these methods and their type parameter issues


    // ItemStack conversion

    default @NotNull ItemStack asItemStack() {
        final var builder = ItemStack.builder(material());

        // Apply each component handler to the item in order.
        //todo this is a bit cursed and could use some cleanup for sure
        components()
                .map(comp -> new Pair<>(comp, ItemComponentHandler.from(comp)))
                .sorted(Comparator.comparing(p -> p.getSecond().priority()))
                .forEach(p -> p.getSecond().buildItemStack(p.getFirst(), builder));

        return builder
                .amount(amount())
                .displayName(LanguageProvider.get(translationKey()))
                .meta(meta -> {
                    meta.customModelData(stateId());
                    meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES);
                })
                .build();
    }

    static @NotNull Item fromItemStack(@NotNull ItemStack itemStack) {
        // Makes the assumption that there will not be unhandled items in circulation
        return Objects.requireNonNull(Item.fromId(itemStack.meta().getCustomModelData()));
    }


    // Static helpers

    static @NotNull Collection<@NotNull Item> values() {
        return ItemRegistry.REGISTRY.values();
    }

    static @Nullable Item fromNamespaceId(@NotNull String namespaceId) {
        return ItemRegistry.REGISTRY.get(namespaceId);
    }

    static @Nullable Item fromNamespaceId(@NotNull NamespaceID namespaceId) {
        return ItemRegistry.REGISTRY.get(namespaceId);
    }

    static @Nullable Item fromId(int id) {
        return ItemRegistry.ID_TO_ITEM.get(id);
    }

    static @Nullable Item fromStateId(int stateId) {
        return ItemRegistry.ITEM_STATE_MAP.get(stateId);
    }

}
