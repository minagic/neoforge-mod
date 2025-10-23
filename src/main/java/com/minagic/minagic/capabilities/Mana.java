package com.minagic.minagic.capabilities;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;



public final class Mana {
    private float mana;
    private int maxMana;

    public Mana() {}

    public void tick(Player player) {
        // --- Retrieve Player Class Info ---
        PlayerClass pc = player.getData(ModAttachments.PLAYER_CLASS);

        int computedMax = computeMaxMana(player, pc);

        // Adjust current mana if needed
        if (mana > computedMax) {
            mana = computedMax;
        }

        this.maxMana = computedMax;

        // --- Regeneration Logic Example ---
        boolean restored = false;

        // Sorcerer passive regen
        if (pc.getMainClass() == PlayerClassEnum.SORCERER) {
            restored |= restoreMana(2);
        }
        // Optional: log or effect if mana restored
        if (restored) {
            // Visual or sound feedback here, if needed
        }
    }

    private int computeMaxMana(Player player, PlayerClass pc) {
        int base = switch (pc.getMainClass()) {
            case SORCERER -> 120;
            case CLERIC -> 100;
            case DRUID -> 110;
            case WIZARD -> 90;
            case BARD -> 95;
            default -> 80;
        };

        // Subclass bonuses
        base += pc.getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) * 5;
        base += pc.getSubclassLevel(PlayerSubClassEnum.CLERIC_ORACLE) * 3;
        base += pc.getSubclassLevel(PlayerSubClassEnum.DRUID_SPIRITS) * 2;

        return base;
    }

    public float getMana() {return mana;}

    public boolean drainMana(float amount) {
        boolean success = mana - amount >= 0;
        if (!success) return false;
        mana = Math.max(0f, mana - amount);
        return true;
    }

    public boolean restoreMana(float amount) {
        boolean success = mana + amount <= maxMana;
        mana = Math.min(maxMana, mana + amount);
        return success;
    }

    public int getMaxMana() {return maxMana;}

    public void setMaxMana(int maxMana) {this.maxMana = maxMana;}

    public static final Codec<Mana> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("mana").forGetter(Mana::getMana),
            Codec.INT.fieldOf("maxMana").forGetter(Mana::getMaxMana)
    ).apply(instance, (manaValue, maxManaValue) -> {
        Mana m = new Mana();
        m.setMaxMana(maxManaValue);
        m.restoreMana(manaValue-m.getMana());
        return m;
    }));

}