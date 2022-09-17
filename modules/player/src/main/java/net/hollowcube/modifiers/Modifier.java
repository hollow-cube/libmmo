package net.hollowcube.modifiers;

public interface Modifier<T> {

    T modifierAmount();

    ModifierOperation operation();

    boolean hasExpired();
}