package com.minagic.minagic.common.events.custom.handlers;

import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.common.events.custom.StatCollectEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Objects;

public class StatCollectHandler {
    @SubscribeEvent
    public static void onSpellStatCollect(StatCollectEvent.SpellStatCollectEvent event){
        event.spell().contributeTo(event);
        Objects.requireNonNull(ActivePowerSourceAttachment.getActivePowerSource(event.context().caster)).contributeTo(event);
    }
}
