package net.hollowcube.modifiers;

public record TemporaryModifier<T>(T modifierAmount, ModifierOperation operation, long expiryTimestamp) implements Modifier<T> {

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() > expiryTimestamp;
    }
}
