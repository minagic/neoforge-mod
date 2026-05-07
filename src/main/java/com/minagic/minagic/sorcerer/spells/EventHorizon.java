package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.ChanneledSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;

@AutoDetection.Spell
public class EventHorizon extends ChanneledSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
    public EventHorizon() {
        super(ChanneledSpell.defaultProperties()
                .withSpellName("Event Horizon")
                .withIdName("event_horizon")
                .withManaCost(100)
                .withSimulacraThreshold(100)
                .withCooldown(10));
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
