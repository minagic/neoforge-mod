package com.minagic.minagic.utilities;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class WorldEvents {
    public WorldEvents() {
    }

    @SubscribeEvent
    public void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event) {
        //
    }
}