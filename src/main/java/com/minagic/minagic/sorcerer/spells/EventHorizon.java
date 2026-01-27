package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.ChanneledSpell;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import java.util.List;

public class EventHorizon extends ChanneledSpell {
    public EventHorizon() {
        super();

        this.spellName = "Event Horizon";
        this.manaCost = 100;
        this.simulacraThreshold = 100;
        this.cooldown = 10;
        // simulacraMaxLifetime left to superclass default
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_VOIDBOURNE,
                20
        ));
    }

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {
        System.out.println("[EventHorizon] cast called for spell: " + getString());
    }

}
