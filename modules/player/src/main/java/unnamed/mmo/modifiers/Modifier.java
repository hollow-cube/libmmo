package unnamed.mmo.modifiers;

public interface Modifier<T> {

    T modifierAmount();

    ModifierOperation operation();

    boolean hasExpired();
}
