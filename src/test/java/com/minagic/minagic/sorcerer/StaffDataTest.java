package com.minagic.minagic.sorcerer;

import com.minagic.minagic.spellCasting.spellslots.SpellSlot;
import com.minagic.minagic.spells.NoneSpell;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StaffDataTest {

    @Test
    void defaultConstructorCreatesTenSlots() {
        StaffData data = new StaffData();

        assertEquals(StaffData.DEFAULT_SIZE, data.getSlots().size(), "Default staff data should allocate ten slots");
        assertEquals(0, data.getCurrentSlot(), "Default slot index should be zero");
        assertNotNull(data.getActive(), "Active slot should be available");
    }

    @Test
    void codecRoundTripPreservesSlotCountAndSelection() {
        StaffData original = new StaffData();
        original.setCurrentSlot(3);

        var encodedResult = StaffData.CODEC.encodeStart(JsonOps.INSTANCE, original).result();
        assertTrue(encodedResult.isPresent(), "Codec should encode staff data");

        var decoded = StaffData.CODEC.parse(JsonOps.INSTANCE, encodedResult.get()).result().orElseThrow();
        assertEquals(original.getSlots().size(), decoded.getSlots().size(), "Slot count should survive codec round-trip");
        assertEquals(original.getCurrentSlot(), decoded.getCurrentSlot(), "Selected slot index should remain the same");
    }

    @Test
    void customConstructorUsesProvidedSlots() {
        SpellSlot custom = new SpellSlot(new NoneSpell());
        StaffData data = new StaffData(List.of(custom), 0);

        assertEquals(1, data.getSlots().size());
        assertSame(custom, data.getSlots().get(0));
    }
}
