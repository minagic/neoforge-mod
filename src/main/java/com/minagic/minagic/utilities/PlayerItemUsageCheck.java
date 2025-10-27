package com.minagic.minagic.utilities;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayerItemUsageCheck {
    boolean wasUsingLastTick;
    ItemStack usingStackLastTick = ItemStack.EMPTY;


    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        boolean isUsing = player.isUsingItem();
        ItemStack current = isUsing ? player.getUseItem() : ItemStack.EMPTY;

        if (!isUsing && wasUsingLastTick) {
            // RELEASED
//            if (usingStackLastTick.getItem() instanceof SpellcastingItem<?> item)
//                item.releaseUsing(usingStackLastTick, player.level(), player);
        }

        wasUsingLastTick = isUsing;
        usingStackLastTick = current;
    }
}
