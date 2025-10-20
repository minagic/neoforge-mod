package com.minagic.minagic.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void handleDataOnMain(SpellSlotCyclePacket payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            // get the item in the player's main hand
            ItemStack stack = serverPlayer.getMainHandItem();
            if (stack.getItem() instanceof com.minagic.minagic.spellCasting.SpellcastingItem spellcastingItem) {
                spellcastingItem.cycleActiveSpellSlot(serverPlayer);

            }
        }
    }

    // Packet handler for SpellSlotCycleDownPacket

    public static void handleCycleSpellDown(SpellSlotCycleDownPacket payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            // get the item in the player's main hand
            ItemStack stack = serverPlayer.getMainHandItem();
            if (stack.getItem() instanceof com.minagic.minagic.spellCasting.SpellcastingItem spellcastingItem) {
                spellcastingItem.cycleActiveSpellSlotDown(serverPlayer);
            }
        }
    }
}
