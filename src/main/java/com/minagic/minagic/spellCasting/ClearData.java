package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ClearData {
    public ClearData() {
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player))
            return;
        SimulacraAttachment.clearSimulacra(player);
        SimulacraAttachment.clearChanneling(player);
        Minagic.LOGGER.debug("Cleared player simulacra for logout: {}", player.getName().getString());

    }
}
