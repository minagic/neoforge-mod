package com.minagic.minagic.api;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.gui.SpellEditorScreen;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.CooldownAttachment;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.packets.SpellWritePacket;
import com.minagic.minagic.packets.SyncSpellcastingDataPacket;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellCasting.spellslots.SpellSlot;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SpellcastingItem<T extends SpellcastingItemData> extends Item {
    protected final DataComponentType<T> type;
    private final Supplier<T> factory;

    protected SpellcastingItem(Properties properties, DataComponentType<T> type, Supplier<T> factory) {
        super(properties);

        this.type = type;
        this.factory = factory;
    }

    public DataComponentType<T> getType() {
        return type;
    }

    public T getData(ItemStack stack) {
        if (!stack.has(type)) {
            setData(stack, factory.get());
        }

        return stack.get(type);
    }

    public SpellSlot getActive(ItemStack stack) {
        return this.getData(stack).getActive();
    }

    @SuppressWarnings("unchecked")
    protected void setData(ItemStack stack, T data) {
        if (stack == null || data == null) return;
        T newData = (T) data.copy();
        stack.set(type, newData);
    }

    public void cycleSlotUp(@Nullable LivingEntity player, ItemStack stack) {
        cycleSlot(player, stack, 1);
    }

    public void cycleSlotDown(@Nullable LivingEntity player, ItemStack stack) {
        cycleSlot(player, stack, -1);
    }

    private void cycleSlot(@Nullable LivingEntity player, ItemStack stack, int direction) {
        if (player == null) {
            return;
        }
        SimulacraAttachment.clearChanneling(player);
        if (player.isUsingItem()) {
            releaseUsing(stack, player.level(), player, 0);
        }

        T data = getData(stack);
        if (data.getSlots().isEmpty()) {
            return;
        }

        int newSlot = Math.floorMod(data.getCurrentSlot() + direction, data.getSlots().size());
        data.setCurrentSlot(newSlot);
        setData(stack, data);

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.getCurrentSlot() + ": " + data.getActive().getEnterPhrase()
            ));
            PacketDistributor.sendToPlayer(serverPlayer, new SyncSpellcastingDataPacket(stack));
        }
    }


    public double getRemainingCooldown(ItemStack stack, LivingEntity living) {
        T data = getData(stack);
        ResourceLocation spellId = data.getActive().getSpellId();

        int tickCooldown = CooldownAttachment.getCooldown(living, spellId);

        return Math.floor((tickCooldown) / 2.0) / 10.0;
    }


    public void writeSpell(ItemStack stack, Level level, LivingEntity player, int slotIndex, Spell spell) {
        if (level.isClientSide()) {
            Minagic.LOGGER.debug("[-SPELL WRITE-] Client-side write request, forwarding to server for slot {}", slotIndex);
            ClientPacketDistributor.sendToServer(new SpellWritePacket(slotIndex, ModSpells.getId(spell)));
            return;
        }
        Minagic.LOGGER.debug("[-SPELL WRITE-] Server-side write request accepted for slot {} with spell {}",
                slotIndex,
                spell == null ? "null" : spell.getString());

        T data = getData(stack);
        Minagic.LOGGER.trace("[-SPELL WRITE-] Resolved stack data type: {}", data.getClass());
        if (slotIndex < 0 || slotIndex >= data.getSlots().size()) {
            return;
        }
        SpellSlot slot = data.getSlots().get(slotIndex);
        slot.setSpell(spell);
        slot.resolveSpell();
        Minagic.LOGGER.trace("[-SPELL WRITE-] Slot {} now stores spell {}",
                slotIndex,
                slot.getSpell() == null ? "null" : slot.getSpell().getString());
        Minagic.LOGGER.trace("[-SPELL WRITE-] Writing updated spell data back to stack");
        setData(stack, data);

        // sync to clients holding this item
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncSpellcastingDataPacket(stack));
        }
    }

    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return false;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }

        // check if player can use this staff
        PlayerClass playerClass = player.getData(ModAttachments.PLAYER_CLASS);
        if (!canPlayerClassUseSpellcastingItem(playerClass)) {
            serverPlayer.sendSystemMessage(Component.literal("You have zero idea on how to use this..."));
            return InteractionResult.FAIL;
        }


        ItemStack stack = player.getItemInHand(hand);
        T data = getData(stack);

        SpellCastContext context = new SpellCastContext(serverPlayer);

        data.getActive().getSpell().perform(SpellEventPhase.START, context, null);
        serverPlayer.startUsingItem(hand);

        return InteractionResult.SUCCESS;
    }


    public InteractionResult use(@NotNull Level level, @NotNull LivingEntity player, @NotNull InteractionHand hand) {
        // check if player can use this staff
        if (player.level().isClientSide()) return InteractionResult.FAIL;

        ItemStack stack = player.getItemInHand(hand);
        T data = getData(stack);

        SpellCastContext context = new SpellCastContext(player);

        data.getActive().getSpell().perform(SpellEventPhase.START, context, null);
        player.startUsingItem(hand);

        return InteractionResult.SUCCESS;
    }

    // These are use lifecycle methods, RMB usage does not work without them
    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.SPYGLASS;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity player) {
        return 72000;
    }

    @Override
    public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity player, int timeLeft) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        if (!(stack.getItem() instanceof SpellcastingItem)) {
            return false;
        }
        T data = getData(stack);

        SpellCastContext context = new SpellCastContext(serverPlayer);
        data.getActive().getSpell().perform(SpellEventPhase.STOP, context, null);
        setData(stack, data);

        return true;
    }

    // GUI Editor Screen
    @SuppressWarnings("unchecked")
    public <S extends SpellEditorScreen<T>> S getEditorScreen(Player player, ItemStack stack) {
        return (S) new SpellEditorScreen<>(player, this, stack);
    }


}
