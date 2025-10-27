package com.minagic.minagic.spellCasting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ClearData {
    public ClearData() {}

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) return;
        var data = player.getData(com.minagic.minagic.registries.ModAttachments.PLAYER_SIMULACRA.get());
        data.clearSimulacra();
        data.clearChanneling();
        player.setData(com.minagic.minagic.registries.ModAttachments.PLAYER_SIMULACRA.get(), data);
        System.out.println("[Minagic] Cleared player simulacra for logout: " + player.getName().getString());

    }
}
