package unnamed.mmo.modifiers;

public class PermanentModifier<T> implements Modifier<T> {

    private final T modifierAmount;
    private final ModifierOperation operation;

    public PermanentModifier(T modifierAmount) {
        this(modifierAmount, ModifierOperation.ADD);
    }

    public PermanentModifier(T modifierAmount, ModifierOperation operation) {
        this.modifierAmount = modifierAmount;
        this.operation = operation;
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
        return false;
    }
}
