package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.ChanneledSpell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class EventHorizon extends ChanneledSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
    public EventHorizon() {
        super();

        this.spellName = "Event Horizon";
        this.manaCost = 100;
        this.simulacraThreshold = 100;
        this.cooldown = 10;
        // simulacraMaxLifetime left to superclass default
    }

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {
        Minagic.LOGGER.debug("Event Horizon cast invoked for {}", getString());
    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_VOIDBOURNE;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 20;
    }
}
