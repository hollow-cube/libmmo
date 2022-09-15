package unnamed.mmo.player;

import org.junit.jupiter.api.Test;
import unnamed.mmo.modifiers.ModifierType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestModifierRegistry {

    @Test
    public void loadRegistry() {
        assertTrue(ModifierType.doesModifierExist("luck"));
        assertEquals(ModifierType.getBaseValue("potatoes"), 10d);
    }

}
