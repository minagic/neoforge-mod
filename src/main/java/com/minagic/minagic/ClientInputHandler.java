package com.minagic.minagic;

//import com.minagic.minagic.gui.StaffSpellScreen;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.packets.SpellSlotCycleDownPacket;
import com.minagic.minagic.packets.SpellSlotCyclePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ClientInputHandler {
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {

        if (ClientKeybinds.CYCLE_SPELL != null && ClientKeybinds.CYCLE_SPELL.consumeClick()) {
            ClientPacketDistributor.sendToServer(new SpellSlotCyclePacket());
        }

        if (ClientKeybinds.CYCLE_SPELL_DOWN != null && ClientKeybinds.CYCLE_SPELL_DOWN.consumeClick()) {
            ClientPacketDistributor.sendToServer(new SpellSlotCycleDownPacket());
        }

        if (ClientKeybinds.SHOW_SPELL_HUD != null && ClientKeybinds.SHOW_SPELL_HUD.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null) {
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof SpellcastingItem<?> spellcastingItem) {
                minecraft.setScreen(spellcastingItem.getEditorScreen(player, stack));
            }
        }
    }
}
