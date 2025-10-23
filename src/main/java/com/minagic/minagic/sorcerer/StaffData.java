package com.minagic.minagic.sorcerer;

import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spellCasting.SpellSlot;
import com.minagic.minagic.spells.Fireball;

// StaffData.java
import com.minagic.minagic.spells.ISpell;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// StaffData.java
public record StaffData(SpellSlot[] slots, int currentSlot) {

    public static final int DEFAULT_SIZE = 10;

    public StaffData {
        // Defensive copy + clamp currentSlot
        Objects.requireNonNull(slots, "slots");
        slots = slots.clone();
        if (slots.length == 0) throw new IllegalArgumentException("SpellSlots array must not be empty");
    }

    public static StaffData empty() {
        System.out.println("Now creating an empty StaffData");
        SpellSlot[] arr = new SpellSlot[DEFAULT_SIZE];
        for (int i = 0; i < arr.length; i++) arr[i] = new SpellSlot();

        return new StaffData(arr, 0);
    }

    public SpellSlot getActive() {
        int idx = Mth.clamp(currentSlot, 0, slots.length - 1);
        return slots[idx];
    }

    public StaffData withCurrentSlot(int next) {
        int idx = Math.floorMod(next, slots.length);
        return new StaffData(slots, idx);
    }

    public StaffData cycleUp() {
        return withCurrentSlot(currentSlot + 1); }
    public StaffData cycleDown() { return withCurrentSlot(currentSlot - 1); }

    // ----- CODEC -----
    public static final Codec<StaffData> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    SpellSlot.CODEC.listOf().fieldOf("slots").forGetter(c -> Arrays.asList(c.slots)),
                    Codec.INT.fieldOf("currentSlot").forGetter(StaffData::currentSlot)
            ).apply(inst, (list, cur) -> new StaffData(list.toArray(new SpellSlot[DEFAULT_SIZE]), cur)));
}