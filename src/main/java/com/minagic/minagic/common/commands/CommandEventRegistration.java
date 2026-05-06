package com.minagic.minagic.common.commands;

import com.minagic.minagic.utilities.ClearAttachmentsCommand;
import com.minagic.minagic.utilities.SetClassCommand;
import com.minagic.minagic.utilities.SorceryPowerCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandEventRegistration {
    // Command registration method

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SetClassCommand.register(event.getDispatcher());
        ClearAttachmentsCommand.register(event.getDispatcher());
        SorceryPowerCommand.register(event.getDispatcher());
    }
}
