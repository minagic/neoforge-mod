package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.packets.SpellWritePacket;
import com.minagic.minagic.packets.SyncSpellcastingDataPacket;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellCasting.SpellSlot;
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

import java.util.Optional;
import java.util.function.Supplier;

public class SpellcastingItem<T extends SpellcastingItemData> extends Item  {
    protected final DataComponentType<T> type;
    private final Supplier<T> factory;

    public DataComponentType<T> getType() {
        return type;
    }

    protected SpellcastingItem(Properties properties, DataComponentType<T> type, Supplier<T> factory) {
        super(properties);

        this.type = type;
        this.factory = factory;
    }
    protected T getData(ItemStack stack){
        //System.out.println("[-GET DATA-] Getting data for SpellcastingItem from stack");
        // if no data present, initialize it
        //System.out.println("[-GET DATA-] Expecting this data component: " + type);
        if (!stack.has(type)) {
            //System.out.print("[-GET DATA-] No data component found in stack. Initializing new data component: ");
            //System.out.println(factory.get());
            setData(stack, factory.get());
        }
        else {
            //System.out.println("[-GET DATA-] Data component found in stack.");
        }

        //System.out.println("[-GET DATA-] RECEIVED DATA Type: " + stack.get(type).getClass());
        //System.out.println("[-GET DATA-] RECEIVED DATA Content: " + stack.get(type));

        return stack.get(type);
    }

    protected void setData(ItemStack stack, T data) {
        if (stack == null || data == null) return;

        // Defensive copy to force a new reference

        T newData = (T) data.copy();
        //System.out.println("[-SET DATA-] Setting data for Spellcasting Item in stack");
        //System.out.println("[-SET DATA-] New data Type: " + newData.getClass());
        //System.out.println("[-SET DATA-] New data Content: " + newData);

        stack.set(type, newData);
    }

    public void cycleSlotUp(Optional<Player> player, ItemStack stack) {
        //System.out.println("[-CYCLING ACTIVE SPELLSLOT UP-] Setting data for Spellcasting Item in stack");

        T data = getData(stack);
        //System.out.println("[-CYCLING ACTIVE SPELLSLOT UP-] New active slot: " + data.getCurrentSlot() + " Spell: " + data.getActive().getSpell().getString());
        int newSlot = Math.floorMod(data.getCurrentSlot() + 1, data.getSlots().size());
        data.setCurrentSlot(newSlot);

        if (player.isPresent()) {
            if (!(player.get() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.getCurrentSlot() + ": " + data.getActive().getEnterPhrase()
            ));
        }

        //System.out.println("[-CYCLING ACTIVE SPELLSLOT UP-] Setting updated data back to stack, data type: " + data.getClass());

        setData(stack, data);
        if (player.isPresent()) {
            if (player.get() instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncSpellcastingDataPacket(stack));
            }
        }

    }

    public void cycleSlotDown(Optional<Player> player, ItemStack stack) {
        //System.out.println("[-CYCLING ACTIVE SPELLSLOT DOWN UP-] Setting data for Spellcasting Item in stack");

        T data = getData(stack);
        //System.out.println("[-CYCLING ACTIVE SPELLSLOT DOWN-] New active slot: " + data.getCurrentSlot() + " Spell: " + data.getActive().getSpell().getString());
        int newSlot = Math.floorMod(data.getCurrentSlot() - 1, data.getSlots().size());
        data.setCurrentSlot(newSlot);

        if (player.isPresent()) {
            if (!(player.get() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.sendSystemMessage(Component.literal(
                    "Switched to slot " + data.getCurrentSlot() + ": " + data.getActive().getEnterPhrase()
            ));
        }

        //System.out.println("[-CYCLING ACTIVE SPELLSLOT DOWN-] Setting updated data back to stack, data type: " + data.getClass());

        setData(stack, data);

        // sync to clients holding this item

        if (player.isPresent()) {
            if (player.get() instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncSpellcastingDataPacket(stack));
            }
        }

    }


    public double getRemainingCooldown(ItemStack stack, Player player) {
        T data = getData(stack);
        ResourceLocation spellId = data.getActive().getSpellId();

        int tickCooldown = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get()).getCooldown(spellId);

        return Math.floor((tickCooldown)/2.0)/10.0;
    }


    public void writeSpell(ItemStack stack, Level level, Player player, int slotIndex, Spell spell) {
        if (level.isClientSide()) {
            //System.out.println("[-SPELL WRITE-] Client side write spell attempt! Redirecting to server via packet.");
            ClientPacketDistributor.sendToServer(new SpellWritePacket(slotIndex, ModSpells.getId(spell)));
            return;
        }
        //System.out.println("[-SPELL WRITE-] Server side write spell attempt, proceeding.");
        //System.out.println("[-SPELL WRITE-] Writing spell " + (spell == null ? "null" : spell.getString()) + " to slot " + slotIndex);

        T data = getData(stack);
        //System.out.println("[-SPELL WRITE-] Resolved data created from stack: " + data.getClass());
        if (slotIndex < 0 || slotIndex >= data.getSlots().size()) {
            return;
        }
        SpellSlot slot = data.getSlots().get(slotIndex);
        slot.setSpell(spell);
        slot.resolveSpell();
        //System.out.println("[-SPELL WRITE-] Updated slot " + slotIndex + " with spell " + (slot.getSpell() == null ? "null" : slot.getSpell().getString()));
        //System.out.println("[-SPELL WRITE-] Setting updated data back to stack, data: " + data);
        setData(stack, data);

        // sync to clients holding this item
        ServerPlayer serverPlayer = (ServerPlayer) player;
        PacketDistributor.sendToPlayer(serverPlayer, new SyncSpellcastingDataPacket(stack));
    }


    public String getActiveSpellSlotKey(ItemStack stack) {
        T data = getData(stack);
        SpellSlot slot = data.getActive();
        //System.out.println("[-GET ACTIVE SPELL SLOT KEY-] Active slot: " + data.getCurrentSlot() + " Spell: " + slot.getSpell().getString());
        return slot.getEnterPhrase();
    }


    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        System.out.println("[-SPELLCASTING ITEM USE-] Attempting to use spellcasting item");
        if (! (player instanceof ServerPlayer serverPlayer)) {
            //System.out.println("[-SPELLCASTING ITEM USE-] Player is not a ServerPlayer, aborting.");
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
        //System.out.println("[-SPELLCASTING ITEM USE-] Retrieved data from item stack: " + data.getClass());
        //System.out.println("[-SPELLCASTING ITEM USE-] Data content: " + data);
        //System.out.println("[-SPELLCASTING ITEM USE-] Active spell slot: " + data.getCurrentSlot() + " Spell: " + data.getActive().getSpell().getString());

        SpellCastContext context = new SpellCastContext(serverPlayer, level, player.getItemInHand(hand));

        data.getActive().onStart(context);
        serverPlayer.startUsingItem(hand);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity player) {
        return 72000;
    }

    @Override
    public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity player, int timeLeft) {
        System.out.println("[-SPELLCASTING ITEM RELEASE USING-] Attempting to stop using spellcasting item");
        if (! (player instanceof ServerPlayer serverPlayer)) {
            //System.out.println("[-SPELLCASTING ITEM RELEASE USING-] Player is not a ServerPlayer, aborting.");
            return false;
        }

        if (!(stack.getItem() instanceof SpellcastingItem)) {
            return false;
        }
        T data = getData(stack);
        //System.out.println("[-SPELLCASTING ITEM RELEASE USING-] Retrieved data from item stack: " + data.getClass());
        //System.out.println("[-SPELLCASTING ITEM RELEASE USING-] Data content: " + data);
        //System.out.println("[-SPELLCASTING ITEM RELEASE USING-] Active spell slot: " + data.getCurrentSlot() + " Spell: " + data.getActive().getSpell().getString());

        SpellCastContext context = new SpellCastContext(serverPlayer, level, player.getItemInHand(player.getUsedItemHand()));
        data.getActive().onStop(context);
        setData(stack, data);

        return true;
    }
    @SuppressWarnings("unchecked")
    public <S extends SpellEditorScreen<T>> S getEditorScreen(Player player, ItemStack stack) {
        //System.out.println("Opening spell editor screen via SpellcastingItem for player " + player.getName().getString());
        return (S) new SpellEditorScreen<>(player, this, stack);
    }

}
