package com.minagic.minagic.sorcerer;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellSlot;
import com.minagic.minagic.spellCasting.SpellcastingItem;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class sorcererStaff extends Item implements SpellcastingItem {

    public sorcererStaff(Properties properties) {
        super(properties);
    }

    private StaffData getData(ItemStack stack) {
        StaffData d = stack.get(ModDataComponents.STAFF_DATA.get());
        if (d == null) {
            d = StaffData.empty();
            stack.set(ModDataComponents.STAFF_DATA.get(), d);
        }
        return d;
    }

    @Override
    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return playerClass.getMainClass() == PlayerClassEnum.SORCERER;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (! (player instanceof ServerPlayer serverPlayer)) return InteractionResult.FAIL;

        // check if player can use this staff
        PlayerClass playerClass = player.getData(ModAttachments.PLAYER_CLASS);
        if (!canPlayerClassUseSpellcastingItem(playerClass)) {
            serverPlayer.sendSystemMessage(Component.literal("You have zero idea on how to use this..."));
            return InteractionResult.FAIL;
        }
        ItemStack stack = player.getItemInHand(hand);
        StaffData data = getData(stack);

        SpellCastContext context = new SpellCastContext(serverPlayer, level);
        data.getActive().cast(context);

        return super.use(level, player, hand);
    }

    @Override
    public void cycleActiveSpellSlot(Optional<Player> player, ItemStack stack) {


        StaffData data = getData(stack);
        data = data.cycleUp();
        SpellSlot slot = data.slots()[data.currentSlot()];

        if (player.isPresent()) {
            if (!(player.get() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.currentSlot() + ": " + slot.getEnterPhrase()
            ));
        }

        stack.set(ModDataComponents.STAFF_DATA.get(), data);
    }


    @Override
    public void cycleActiveSpellSlotDown(Optional<Player> player, ItemStack stack) {

        StaffData data = getData(stack);
        data = data.cycleDown();
        SpellSlot slot = data.slots()[data.currentSlot()];

        if (player.isPresent()) {
            if (!(player.get() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.currentSlot() + ": " + slot.getEnterPhrase()
            ));
        }

        stack.set(ModDataComponents.STAFF_DATA.get(), data);

    }

    @Override
    public String getActiveSpellSlotKey(ItemStack stack) {

        StaffData data = getData(stack);
        System.out.println("this is SorcererStaff reporting: Getting active spell slot key: " + data.getActive().getSpell().getString());
        System.out.println(data.getActive().getSpell());
        System.out.println(data.getActive());
        return data.getActive().getSpell().getString();
    }

    @Override
    public double getRemainingCooldown(ItemStack stack, Player player) {
        StaffData data = getData(stack);
        ResourceLocation spellId = data.getActive().getSpellId();

        int tickCooldown = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get()).getCooldown(spellId);

        return Math.floor((tickCooldown)/2.0)/10.0;
    }

    @Override
    public void writeSpell(ItemStack stack, Level level, int slotIndex, com.minagic.minagic.spells.ISpell spell) {
        System.out.println("Writing spell " + (spell == null ? "null" : spell.getString()) + " to slot " + slotIndex);

        StaffData data = getData(stack);
        if (slotIndex < 0 || slotIndex >= data.slots().length) {
            return;
        }
        SpellSlot slot = data.getActive();
        slot.setSpell(spell);
        stack.set(ModDataComponents.STAFF_DATA, data);
        cycleActiveSpellSlot(Optional.empty(), stack);
        cycleActiveSpellSlotDown(Optional.empty(), stack);
    }
}