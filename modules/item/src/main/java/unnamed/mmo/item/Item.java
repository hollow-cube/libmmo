package unnamed.mmo.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import unnamed.mmo.lang.LanguageProvider;
import unnamed.mmo.registry.Resource;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A custom item. This system works similarly to {@link net.minestom.server.instance.block.Block}, where
 * each item has a unique ID, and can have multiple states, each of which gave their own unique ID and some
 * property overrides.
 * <p>
 * The {@link #stateId()} is used as the custom model data of the {@link ItemStack}, and should be used
 * to identify an {@link Item} from an {@link ItemStack}. For this conversion, {@link #fromItemStack(ItemStack)}
 * should always be used.
 */
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


    /**
     * @return The ID of this item. Multiple item IDs may exist with different states.
     */
    @Override
    @Contract(pure = true)
    int id();

    /**
     * Returns the unique ID of this item state.
     */
    @Contract(pure = true)
    int stateId();

    /**
     * todo need to add withProperties to create a state from properties
     *
     * @return The property map for this item.
     */
    @Unmodifiable @NotNull Map<String, String> properties();


    /**
     * Returns the translation key for this item
     * @see LanguageProvider#get(Component)
     */
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

    /**
     * Fetches an {@link ItemComponent} by its type
     *
     * @return The component, or null if not found.
     */
    default <C extends ItemComponent> @Nullable C getComponent(Class<C> type) {
        return getComponent(ItemComponentHandler.from(type).name());
    }

    /**
     * Fetches an {@link ItemComponent} by its namespace id.
     * <p>
     * todo might want to consider removing these methods and their type parameter issues
     *
     * @return The component, or null if not found.
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    <C extends ItemComponent> @Nullable C getComponent(@NotNull String namespace);


    // ItemStack conversion

    /**
     * Returns an {@link ItemStack} which may be given to the player, contains all transient item data.
     */
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
                .displayName(LanguageProvider.get(Component.translatable(translationKey())))
                .meta(meta -> {
                    meta.customModelData(stateId());
                    meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES);
                })
                .build();
    }

    /**
     * Parses an {@link ItemStack} into an {@link Item}. This should be used over a manual item lookup
     * because it correctly loads transient item data.
     */
    static @NotNull Item fromItemStack(@NotNull ItemStack itemStack) {
        // Makes the assumption that there will not be unhandled items in circulation
        return Objects.requireNonNull(Item.fromId(itemStack.meta().getCustomModelData()))
                .withAmount(itemStack.amount());
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
