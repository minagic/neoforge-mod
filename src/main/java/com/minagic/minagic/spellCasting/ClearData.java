package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ClearData {
    public ClearData() {}

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) return;
        PlayerSimulacraAttachment.clearSimulacra(player, player.level());
        PlayerSimulacraAttachment.clearChanneling(player, player.level());

        System.out.println("[Minagic] Cleared player simulacra for logout: " + player.getName().getString());

    }
}
