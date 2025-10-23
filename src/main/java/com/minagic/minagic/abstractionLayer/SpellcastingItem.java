package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModDataComponents;
import com.minagic.minagic.sorcerer.StaffData;
import com.minagic.minagic.spellCasting.ISpellcastingItem;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellCasting.SpellSlot;
import com.minagic.minagic.spells.ISpell;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.function.Supplier;

public class SpellcastingItem<T extends SpellcastingItemData> extends Item implements ISpellcastingItem {
    protected final DataComponentType<T> type;
    private final Supplier<T> factory;

    protected SpellcastingItem(Properties properties, DataComponentType<T> type, Supplier<T> factory) {
        super(properties);

        this.type = type;
        this.factory = factory;
    }
    @SuppressWarnings("unchecked")
    protected T getData(ItemStack stack){
        // if no data present, initialize it
        if (!stack.has(type)) {
            System.out.println(factory.get());
            setData(stack, factory.get());
        }
        return stack.get(type);
    }



    protected void setData(ItemStack stack, T data) {
        stack.set(type, data);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void cycleActiveSpellSlot(Optional<Player> player, ItemStack stack) {
        T data = getData(stack);
        data = (T) data.cycleUp();
        SpellSlot slot = data.getActive();

        if (player.isPresent()) {
            if (!(player.get() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.getCurrentSlot() + ": " + slot.getEnterPhrase()
            ));
        }

        setData(stack, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cycleActiveSpellSlotDown(Optional<Player> player, ItemStack stack) {
        // similar to cycleActiveSpellSlot but cycles down
        T data = getData(stack);
        data = (T) data.cycleDown();
        SpellSlot slot = data.getActive();

        if (player.isPresent()) {
            if (!(player.get() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.getCurrentSlot() + ": " + slot.getEnterPhrase()
            ));
        }
        setData(stack, data);
    }

    @Override
    public double getRemainingCooldown(ItemStack stack, Player player) {
        SpellcastingItemData data = getData(stack);
        ResourceLocation spellId = data.getActive().getSpellId();

        int tickCooldown = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get()).getCooldown(spellId);

        return Math.floor((tickCooldown)/2.0)/10.0;
    }

    @Override
    public void writeSpell(ItemStack stack, Level level, int slotIndex, ISpell spell) {
        System.out.println("Writing spell " + (spell == null ? "null" : spell.getString()) + " to slot " + slotIndex);

        T data = getData(stack);
        if (slotIndex < 0 || slotIndex >= data.getSlots().size()) {
            return;
        }
        SpellSlot slot = data.getActive();
        slot.setSpell(spell);
        setData(stack, data);
        cycleActiveSpellSlot(Optional.empty(), stack);
        cycleActiveSpellSlotDown(Optional.empty(), stack);
    }

    @Override
    public String getActiveSpellSlotKey(ItemStack stack) {
        T data = getData(stack);
        SpellSlot slot = data.getActive();
        return slot.getEnterPhrase();
    }

    @Override
    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return false;
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
        SpellcastingItemData data = getData(stack);

        SpellCastContext context = new SpellCastContext(serverPlayer, level);
        data.getActive().cast(context);

        return super.use(level, player, hand);
    }

}
