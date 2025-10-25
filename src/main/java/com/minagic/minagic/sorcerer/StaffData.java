package com.minagic.minagic.sorcerer;

import com.minagic.minagic.abstractionLayer.SpellcastingItemData;
import com.minagic.minagic.spellCasting.SpellSlot;
import com.mojang.serialization.Codec;

import java.util.ArrayList;

import java.util.List;

// StaffData.java
public class StaffData extends SpellcastingItemData {

    public static final int DEFAULT_SIZE = 5;

    public StaffData() {
        System.out.println("Creating a default StaffData with "+DEFAULT_SIZE+" slots.");
        this.slots = new ArrayList<>(DEFAULT_SIZE);
        for (int i = 0; i < DEFAULT_SIZE; i++) slots.add(new SpellSlot());
        this.currentSlot = 0;
    }
    public StaffData(List<SpellSlot> slots, Integer currentSlot) {
        super(slots, currentSlot);
        System.out.println("Creating a StaffData with "+slots.size()+" slots and currentSlot "+currentSlot);
    }
    public static final Codec<StaffData> CODEC =
            SpellcastingItemData.codec(StaffData::new);
}