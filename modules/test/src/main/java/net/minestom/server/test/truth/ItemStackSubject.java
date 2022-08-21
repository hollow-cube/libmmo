package net.minestom.server.test.truth;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ConstantConditions")
public class ItemStackSubject extends Subject {
    private final ItemStack actual;

    public static ItemStackSubject assertThat(@Nullable ItemStack actual) {
        return Truth.assertAbout(itemStacks()).that(actual);
    }

    protected ItemStackSubject(FailureMetadata metadata, @Nullable ItemStack actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void hasAmount(int expectedAmount) {
        check("amount()").that(actual.amount()).isEqualTo(expectedAmount);
    }


    public static Factory<ItemStackSubject, ItemStack> itemStacks() {
        return ItemStackSubject::new;
    }

}
