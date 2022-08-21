package net.minestom.server.test.truth;

import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.truth.Truth.assertAbout;

public class AbstractInventorySubject extends Subject {
    private final AbstractInventory actual;

    public static AbstractInventorySubject assertThat(AbstractInventory actual) {
        return assertAbout(abstractInventories()).that(actual);
    }

    protected AbstractInventorySubject(FailureMetadata metadata, @Nullable AbstractInventory actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void isEmpty() {
        if (!itemStacks().isEmpty()) {
            failWithActual(Fact.simpleFact("expected to be empty"));
        }
    }

    public void isNotEmpty() {
        if (itemStacks().isEmpty()) {
            failWithActual(Fact.simpleFact("expected not to be empty"));
        }
    }

    public void containsExactly(@NotNull ItemStack... itemStacks) {
        Truth.assertThat(itemStacks()).containsExactly((Object[]) itemStacks);
    }

    public void doesNotContain(@NotNull ItemStack itemStack) {
        Truth.assertThat(itemStacks()).doesNotContain(itemStack);
    }

    private List<ItemStack> itemStacks() {
        return Arrays.stream(actual.getItemStacks())
                .filter(Predicate.not(ItemStack::isAir))
                .toList();
    }

    private static Factory<AbstractInventorySubject, AbstractInventory> abstractInventories() {
        return AbstractInventorySubject::new;
    }

}
