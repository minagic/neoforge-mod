package com.minagic.minagic.common.events;

import com.minagic.minagic.capabilities.AttachmentDispatcher;
import com.minagic.minagic.common.commands.CommandEventRegistration;
import com.minagic.minagic.events.NeoForgeEventHandler;
import com.minagic.minagic.spellCasting.ClearData;
import com.minagic.minagic.utilities.EntityFreezer;
import com.minagic.minagic.utilities.ModEvents;
import com.minagic.minagic.utilities.WorldEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class CommonEventRegistration {
    public static final EntityFreezer ENTITY_FREEZER = new EntityFreezer();

    private CommonEventRegistration() {}

    public static void register() {
        NeoForge.EVENT_BUS.register(new WorldEvents());
        NeoForge.EVENT_BUS.register(new ClearData());
        NeoForge.EVENT_BUS.register(ENTITY_FREEZER);
        NeoForge.EVENT_BUS.register(new ModEvents());
        NeoForge.EVENT_BUS.register(NeoForgeEventHandler.class);
        NeoForge.EVENT_BUS.register(AttachmentDispatcher.class);
        NeoForge.EVENT_BUS.register(CommandEventRegistration.class);
    }
}
