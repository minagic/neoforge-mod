package com.minagic.minagic.wizard;

import com.minagic.minagic.api.SpellcastingItemData;
import com.minagic.minagic.spellCasting.spellslots.SpellSlot;
import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;

public class WizardWandData extends SpellcastingItemData {
    public static final int DEFAULT_SIZE = 10;
    public static final Codec<WizardWandData> CODEC =
            SpellcastingItemData.codec(WizardWandData::new);

    public WizardWandData() {
        this.slots = new ArrayList<>(DEFAULT_SIZE);
        for (int i = 0; i < DEFAULT_SIZE; i++) slots.add(new SpellSlot());
        this.currentSlot = 0;
    }

    public WizardWandData(List<SpellSlot> slots, Integer currentSlot) {
        super(slots, currentSlot);
    }
}
