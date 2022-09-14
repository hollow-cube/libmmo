package net.hollowcube.damage;

import net.minestom.server.attribute.AttributeOperation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// Mostly ripped from my other projects
public class MultiPartValue {

    private final double base;
    private double boost;
    private double multiplierBase;
    private double multiplierTotal;

    public MultiPartValue(double base) {
        this.base = base;
        this.boost = 0;
        this.multiplierBase = 1;
        this.multiplierTotal = 1;
    }

    @Contract(mutates = "this")
    public void addBase(double amount) {
        this.boost += amount;
    }

    /**
     * Multiplies the total value by the amount
     *
     * @param amount The amount to multiply the value by
     */
    @Contract(mutates = "this")
    public void multiply(double amount) {
        multiplierTotal *= amount;
    }

    /**
     * Modifies the current value according to the operation
     *
     * @param amount    The amount to modify by
     * @param operation The operation by which to modify the value
     */
    @Contract(mutates = "this")
    public void modifyValue(double amount, @NotNull AttributeOperation operation) {
        switch (operation) {
            case ADDITION -> addBase(amount);
            case MULTIPLY_BASE -> multiplierBase += amount;
            case MULTIPLY_TOTAL -> multiply(amount);
        }
    }

    /**
     * Calculates and returns the value represented bu this object
     *
     * @return The result of the value calculation
     */
    public double getFinalValue() {
        return (base + boost) * multiplierBase * multiplierTotal;
    }

    @Contract(mutates = "this")
    public void combine(@NotNull MultiPartValue other) {
        addBase(other.base + other.boost);
        this.multiplierBase += other.multiplierBase;
        multiply(other.multiplierTotal);
    }
}
