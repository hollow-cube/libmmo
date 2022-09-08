package unnamed.mmo.modifiers;

import unnamed.mmo.damage.MultiPartValue;

import java.util.HashMap;
import java.util.Map;

public class ModifierList {

    private final Map<String, Modifier<Double>> modifiers = new HashMap<>();
    private final double initialAmount;

    public ModifierList(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public void addPermanentModifier(String id, double amount) {
        modifiers.put(id, new PermanentModifier<>(amount));
    }

    public void addPermanentModifier(String id, double amount, ModifierOperation operation) {
        modifiers.put(id, new PermanentModifier<>(amount, operation));
    }


    public void addTemporaryModifier(String id, double amount, long expiresAt) {
        modifiers.put(id, new TemporaryModifier<>(amount, expiresAt));
    }

    public void addTemporaryModifier(String id, double amount, ModifierOperation operation, long expiresAt) {
        modifiers.put(id, new TemporaryModifier<>(amount, operation, expiresAt));
    }

    public void removeModifier(String id) {
        modifiers.remove(id);
    }

    public double calculateTotal() {
        MultiPartValue value = new MultiPartValue(initialAmount);
        // Cleanup - so all current values are not expired
        modifiers.values().removeIf(Modifier::hasExpired);
        for(var entry : modifiers.entrySet()) {
            Modifier<Double> modifier = entry.getValue();
            switch (modifier.getOperation()) {
                case ADD -> value.addBase(modifier.getModifierAmount());
                case MULTIPLY -> value.multiply(modifier.getModifierAmount());
            }
        }
        return value.getFinalValue();
    }
}
