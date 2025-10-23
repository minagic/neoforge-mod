package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.spellCasting.SpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    // override this method
    public SpellcastingItemData withCurrentSlot(int next) {
        int idx = Math.floorMod(next, slots.size());
        return new SpellcastingItemData(slots, idx);
    }

    // override this method
    public SpellcastingItemData cycleUp() {
        return withCurrentSlot(currentSlot + 1);
    }

    // override this method

    public SpellcastingItemData cycleDown() {
        return withCurrentSlot(currentSlot - 1);
    }

    public List<SpellSlot> getSlots() {return slots;}

    public int getCurrentSlot() {return currentSlot;}

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

    public int hashCode() {
        int result = Integer.hashCode(currentSlot);
        for (SpellSlot slot : slots) {
            result = 31 * result + slot.hashCode();
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SpellcastingItemData{");
        sb.append("currentSlot=").append(currentSlot).append(", slots=[");
        for (int i = 0; i < slots.size(); i++) {
            sb.append(slots.get(i).toString());
            if (i < slots.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    // ---- CODEC ----
    @SuppressWarnings("unchecked")
    public static <T extends SpellcastingItemData> Codec<T> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                SpellSlot.CODEC.listOf().fieldOf("slots").forGetter(SpellcastingItemData::getSlots),
                Codec.INT.fieldOf("currentSlot").forGetter(SpellcastingItemData::getCurrentSlot)
        ).apply(instance, (slots, currentSlot) -> (T)new SpellcastingItemData(slots, currentSlot)
        ));
    }

}
