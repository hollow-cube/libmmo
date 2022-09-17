package net.hollowcube.player;

import net.hollowcube.modifiers.ModifierType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestModifierRegistry {

    @Test
    public void loadRegistry() {
        assertTrue(ModifierType.doesModifierExist("luck"));
        assertEquals(ModifierType.getBaseValue("potatoes"), 10d);
    }

}