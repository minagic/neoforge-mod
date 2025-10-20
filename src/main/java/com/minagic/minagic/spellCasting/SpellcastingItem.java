package com.minagic.minagic.spellCasting;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface SpellcastingItem {
    void tickSpellSlots(Level level, Player player);

    void cycleActiveSpellSlot(Player player);

    void cycleActiveSpellSlotDown(Player player);


}
