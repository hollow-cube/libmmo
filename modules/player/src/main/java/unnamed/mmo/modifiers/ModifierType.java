package unnamed.mmo.modifiers;

public enum ModifierType {
    DIG_SPEED(1),
    MELEE_DAMAGE(0)
    ;

    private final double baseAmount;

    ModifierType(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getBaseAmount() {
        return baseAmount;
    }
}
