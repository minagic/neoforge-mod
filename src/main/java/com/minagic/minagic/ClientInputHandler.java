package com.minagic.minagic;

import com.minagic.minagic.packets.SpellSlotCycleDownPacket;
import com.minagic.minagic.packets.SpellSlotCyclePacket;
import com.minagic.minagic.spellCasting.SpellcastingItem;
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
            // send packet to server to cycle spell
            ClientPacketDistributor.sendToServer(new SpellSlotCyclePacket());

        }

        if (ClientKeybinds.CYCLE_SPELL_DOWN != null && ClientKeybinds.CYCLE_SPELL_DOWN.consumeClick()) {
            // send packet to server to cycle spell down
            ClientPacketDistributor.sendToServer(new SpellSlotCycleDownPacket());
        }
    }
}
