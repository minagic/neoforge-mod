package com.minagic.minagic.sorcerer;

import com.minagic.minagic.spellCasting.SpellSlot;
import com.minagic.minagic.spellCasting.SpellcastingItem;
import com.minagic.minagic.spells.Fireball;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class sorcererStaff extends Item implements SpellcastingItem {
    SpellSlot[] spellSlots = new SpellSlot[10];
    int currentSlot = 0;

    public sorcererStaff(Properties properties) {
        super(properties);
        for (int i = 0; i < spellSlots.length; i++) {
            spellSlots[i] = new SpellSlot();
        }

        // TEMPORARY: Add some default spells to the staff
        spellSlots[0].setSpell(new Fireball());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Generate a spell cast context here
        SpellCastContext context = new SpellCastContext(player, level);

        spellSlots[currentSlot].cast(context);

        return super.use(level, player, hand);
    }

    @Override
    public void tickSpellSlots() {
        for (SpellSlot slot : spellSlots) {
           slot.tickCooldown();
        }
    }
    @Override
    public void cycleActiveSpellSlot(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;


        currentSlot = (currentSlot + 1) % spellSlots.length;
        serverPlayer.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "Switched to slot " + currentSlot + ": " + spellSlots[currentSlot].getEnterPhrase()
                )
        );

    }
    @Override
    public void cycleActiveSpellSlotDown(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;


        currentSlot = (currentSlot - 1 + spellSlots.length) % spellSlots.length;
        serverPlayer.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "Switched to slot " + currentSlot + ": " + spellSlots[currentSlot].getEnterPhrase()
                )
        );

    }

    @Override
    public double getRemainingCooldown() {
        return Math.floor((spellSlots[currentSlot].cooldownRemaining)/2.0)/10.0;
    }


}
