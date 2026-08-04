package com.minagic.minagic.registries;

import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.ShipPowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;

public class ModPowerSources {
    public static void register() {
        ActivePowerSourceAttachment.register(
                new SorceryPowerSourceAttachment().getId(),
                ModAttachments.SORCERY_POWER_SOURCE
        );
        ActivePowerSourceAttachment.register(
                new ShipPowerSourceAttachment().getId(),
                ModAttachments.SHIP_POWER_SOURCE
        );
    }
}
