package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.spellCasting.SpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

// this is a record-like class to hold spellcasting item data such as spell slots and the current active slot
// it is immutable but extensible
public class SpellcastingItemData {
    protected List<SpellSlot> slots;
    protected int currentSlot;

    // OVERRIDE THIS METHOD
    public SpellcastingItemData(List<SpellSlot> slots, int currentSlot){
        this.slots = new ArrayList<>(Objects.requireNonNull(slots, "slots"));
        this.currentSlot = currentSlot;
    }

    public SpellcastingItemData(){
        this.slots = new ArrayList<>();
        this.slots.add(new SpellSlot());
        this.currentSlot = 0;
    }

    public SpellSlot getActive() {
        int idx = Mth.clamp(currentSlot, 0, slots.size() - 1);
        return slots.get(idx);
    }
    public List<SpellSlot> getSlots() {return slots;}

    public int getCurrentSlot() {return currentSlot;}

    public void setCurrentSlot(int currentSlot) {this.currentSlot = currentSlot;}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SpellcastingItemData that)) return false;
        if (this.currentSlot != that.currentSlot) return false;
        if (this.slots.size() != that.slots.size()) return false;
        for (int i = 0; i < this.slots.size(); i++) {
            if (!this.slots.get(i).equals(that.slots.get(i))) return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        int result = Integer.hashCode(currentSlot);
        for (SpellSlot slot : slots) {
            result = 31 * result + slot.hashCode();
        }
        return result;
    }

    public String getRaw(){
        StringBuilder sb = new StringBuilder();
        sb.append("currentSlot:").append(currentSlot).append(";");
        sb.append("slots:");
        for (int i = 0; i < slots.size(); i++) {
            sb.append(slots.get(i).getSpellId());
            if (i < slots.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SpellcastingItemData{");
        sb.append("currentSlot=").append(currentSlot).append(", slots=[");
        for (int i = 0; i < slots.size(); i++) {
            sb.append(slots.get(i).getSpell().toString());
            sb.append("/");
            sb.append(slots.get(i).getSpellId());
            if (i < slots.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    public SpellcastingItemData copy(){
        return new SpellcastingItemData(this.getSlots(), this.getCurrentSlot());
    }

    // ---- CODEC ----
    public static <T extends SpellcastingItemData> Codec<T> codec(
            BiFunction<List<SpellSlot>, Integer, T> factory
    ) {
        return RecordCodecBuilder.create(instance -> instance.group(
                SpellSlot.CODEC.listOf().fieldOf("slots").forGetter(SpellcastingItemData::getSlots),
                Codec.INT.fieldOf("currentSlot").forGetter(SpellcastingItemData::getCurrentSlot)
        ).apply(instance, factory));
    }

}
