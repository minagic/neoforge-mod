package com.minagic.minagic.sorcerer;

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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        StaffData data = getData(stack);

        SpellCastContext context = new SpellCastContext(player, level);
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
        SpellSlot slot = data.getActive();
        String result = slot.getSpell() == null ? "No spell assigned" : slot.getSpell().getClass().getSimpleName();
       // System.out.println("This is sorcerer Staff reporting, spell in active slot is: " + result);
        return data.getActive().getSpell() != null ?
                data.getActive().getSpell().getClass().getSimpleName() :
                "No Spell Assigned";
    }

    @Override
    public double getRemainingCooldown(ItemStack stack, Player player) {
        StaffData data = getData(stack);
        ResourceLocation spellId = data.getActive().getSpellId();

        int tickCooldown = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get()).getCooldown(spellId);

        System.out.println("This is sorcerer Staff reporting, remaining cooldown (in ticks) is: " + tickCooldown);

        return Math.floor((tickCooldown)/2.0)/10.0;
    }

    @Override
    public void writeSpell(ItemStack stack, Level level, int slotIndex, com.minagic.minagic.spells.ISpell spell) {
        StaffData data = getData(stack);
        if (slotIndex < 0 || slotIndex >= data.slots().length) {
            return;
        }
        SpellSlot slot = data.slots()[slotIndex];
        slot.setSpell(spell);
        stack.set(ModDataComponents.STAFF_DATA.get(), data);
        cycleActiveSpellSlot(Optional.empty(), stack);
        cycleActiveSpellSlotDown(Optional.empty(), stack);
    }
}