package unnamed.mmo.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.item.Item;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;

public interface CraftingRecipe {
    /**
     * Checks against the list of items in the current crafting menu to see if the recipe matches
     * @param items A list of items in the crafting menu, ordered from top row (starting from the left), to bottom right
     * @return true if the recipe matches, false if it does not
     */
    boolean doesRecipeMatch(@NotNull List<ItemStack> items);

    @NotNull ItemStack getRecipeOutput();

    boolean containsIngredient(@NotNull ItemStack itemStack);

    record ComponentEntry(Item item, int count) {}

    Codec<ComponentEntry> ENTRY_CODEC = RecordCodecBuilder.create(i -> i.group(
            Item.CODEC.fieldOf("item").forGetter(ComponentEntry::item),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ComponentEntry::count)
    ).apply(i, ComponentEntry::new));
}
