package unnamed.mmo.modifiers;

public class TemporaryModifier<T> implements Modifier<T> {

    private final long expiryTimestamp;
    private final ModifierOperation operation;
    private final T modifierAmount;

    public TemporaryModifier(T modifierAmount, long expiresAt) {
        this(modifierAmount, ModifierOperation.ADD, expiresAt);
    }

    public TemporaryModifier(T modifierAmount, ModifierOperation operation, long expiresAt) {
        this.modifierAmount = modifierAmount;
        this.operation = operation;
        this.expiryTimestamp = expiresAt;
    }

    @Override
    public T getModifierAmount() {
        return modifierAmount;
    }

    @Override
    public ModifierOperation getOperation() {
        return operation;
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() > expiryTimestamp;
    }
}
