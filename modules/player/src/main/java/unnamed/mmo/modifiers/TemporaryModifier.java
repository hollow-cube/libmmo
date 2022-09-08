package unnamed.mmo.modifiers;

public record TemporaryModifier<T>(T modifierAmount, ModifierOperation operation, long expiryTimestamp) implements Modifier<T> {

    @Override
    public T modifierAmount() {
        return modifierAmount;
    }

    @Override
    public ModifierOperation operation() {
        return operation;
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() > expiryTimestamp;
    }
}
