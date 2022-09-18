package net.hollowcube.player;

import net.hollowcube.modifiers.ModifierList;
import net.hollowcube.modifiers.ModifierOperation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestModifierList {

    @Test
    public void testAddingModifiers() {
        ModifierList list = new ModifierList(1);
        list.addPermanentModifier("initial", 1.1, ModifierOperation.MULTIPLY);
        list.addPermanentModifier("wahoo", 3, ModifierOperation.ADD);
        assertEquals(4.4, list.calculateTotal());
    }

    @Test
    public void testRemovingModifiers() {
        ModifierList list = new ModifierList(1);
        list.addPermanentModifier("initial", 1.5, ModifierOperation.MULTIPLY);
        list.addPermanentModifier("wahoo", 3, ModifierOperation.ADD);
        assertEquals(6, list.calculateTotal());
        list.removeModifier("initial");
        assertEquals(4, list.calculateTotal());
    }
}