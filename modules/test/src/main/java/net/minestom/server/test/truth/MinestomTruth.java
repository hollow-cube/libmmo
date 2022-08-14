package net.minestom.server.test.truth;

import com.google.common.truth.Truth;
import net.minestom.server.entity.Entity;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinestomTruth {
    private MinestomTruth() {}

    public static @NotNull EntitySubject assertThat(@Nullable Entity actual) {
        return EntitySubject.assertThat(actual);
    }

    public static @NotNull AbstractInventorySubject assertThat(@Nullable AbstractInventory actual) {
        return AbstractInventorySubject.assertThat(actual);
    }

    public static @NotNull ItemStackSubject assertThat(@Nullable ItemStack actual) {
        return ItemStackSubject.assertThat(actual);
    }

}
