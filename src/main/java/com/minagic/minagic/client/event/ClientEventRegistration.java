package com.minagic.minagic.client.event;

import com.minagic.minagic.client.hud.CooldownOverlay;
import com.minagic.minagic.client.input.ClientInputHandler;
import net.neoforged.bus.api.IEventBus;

public class ClientEventRegistration {

    public static void register(IEventBus neoBus, IEventBus modBus){
        neoBus.register(new ClientInputHandler());
        neoBus.register(new CooldownOverlay());
        modBus.register(ClientModEvents.class);
    }
}
