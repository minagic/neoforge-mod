package com.minagic.minagic.capabilities;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;



public final class Mana {
    private float mana;
    private int maxMana;

    public Mana() {}

    public void changeClass(PlayerClassEnum newClass) {
        switch (newClass) {
            case SORCERER -> maxMana = 100;
            default -> maxMana = 200;
        }
        mana = Math.min(mana, maxMana); // Adjust current mana if needed
    }

    public void tick(Player player) {
        // Example: Regenerate 1 mana every tick up to maxMana
        if (player.getData(ModAttachments.PLAYER_CLASS.get()).getPlayerClass() == PlayerClassEnum.SORCERER){
            restoreMana(0.1f);
        }
        else {
            restoreMana(0.2f);
        }

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