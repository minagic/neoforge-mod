package com.minagic.minagic.testing.spells;

import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class InstantaneousDevSpell extends InstanteneousSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {

        System.out.println("[InstantaneousDevSpell] Cast by " + context.caster.getName().getString()
                + " at " + context.level().dimension().location());
    }

    public InstantaneousDevSpell() {
        super();

        this.spellName = "InstantaneousDevSpell";
        this.manaCost = 10;
        this.cooldown = 40;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 0;
    }

    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? SpellValidator.CastFailureReason.OK : SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}