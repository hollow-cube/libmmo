package unnamed.mmo.modifiers;

public record PermanentModifier<T>(T modifierAmount, ModifierOperation operation) implements Modifier<T> {

    @Override
    public boolean hasExpired() {
        return false;
    }
}
