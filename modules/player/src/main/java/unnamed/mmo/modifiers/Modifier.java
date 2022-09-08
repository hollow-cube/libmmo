package unnamed.mmo.modifiers;

public interface Modifier<T> {

    T getModifierAmount();

    ModifierOperation getOperation();

    boolean hasExpired();
}
