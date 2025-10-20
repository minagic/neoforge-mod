package com.minagic.minagic.spellCasting;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SpellCooldownHandler {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) return;
        for (int i = 0; i < event.getEntity().getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getEntity().getInventory().getItem(i);
            if (stack.getItem() instanceof SpellcastingItem spellcastingItem) {
                spellcastingItem.tickSpellSlots(event.getEntity().level(), event.getEntity());
            }
        }
    }
}
