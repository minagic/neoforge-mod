package com.minagic.minagic.druid;

import com.minagic.minagic.api.SpellcastingItemData;
import com.minagic.minagic.spellCasting.spellslots.SpellSlot;
import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;

public class HornData extends SpellcastingItemData {
    public static final int DEFAULT_SIZE = 10;

    public HornData() {
        System.out.println("Creating a default HornData with "+DEFAULT_SIZE+" slots.");
        this.slots = new ArrayList<>(DEFAULT_SIZE);
        for (int i = 0; i < DEFAULT_SIZE; i++) slots.add(new SpellSlot());
        this.currentSlot = 0;
    }
    public HornData(List<SpellSlot> slots, Integer currentSlot) {
        super(slots, currentSlot);
        System.out.println("Creating a HornData with "+slots.size()+" slots and currentSlot "+currentSlot);
    }
    public static final Codec<HornData> CODEC =
            SpellcastingItemData.codec(HornData::new);
}
