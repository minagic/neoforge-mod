package com.minagic.minagic.utilities;

import com.minagic.minagic.spellCasting.SpellcastingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

// LocalCooldownManager.java
public class LocalCooldownManager {
    // Cooldowns stored as (slot -> cooldown)

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (player.level().isClientSide()) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof SpellcastingItem item){
                    item.tickSpellSlots();

                }
            }
        }
    }

    public static double getRemainingCooldown(ItemStack stack) {

        if (stack.getItem() instanceof SpellcastingItem spellcastingItem) {
            return spellcastingItem.getRemainingCooldown();
        }
        return 0;
    }
}
