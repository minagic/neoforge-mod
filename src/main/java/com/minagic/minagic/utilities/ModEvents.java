package com.minagic.minagic.utilities;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

public class ModEvents {

    @SubscribeEvent
    public void onItemDropped(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();

        if (stack.getItem() instanceof SpellcastingItem<?> && !event.getPlayer().level().isClientSide()) {
            stack.getItem().releaseUsing(stack, event.getPlayer().level(), event.getPlayer(), 0);
        }
    }
}
