package com.minagic.minagic.spellCasting;

import com.minagic.minagic.spells.ISpell;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface SpellcastingItem {

    void cycleActiveSpellSlot(Optional<Player> player, ItemStack stack);

    void cycleActiveSpellSlotDown(Optional<Player> player, ItemStack stack);

    double getRemainingCooldown(ItemStack stack, Player player);

    void writeSpell(ItemStack stack, Level level, int slotIndex, ISpell spell);

    String getActiveSpellSlotKey(ItemStack stack);
}
