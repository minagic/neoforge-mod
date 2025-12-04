package com.minagic.minagic.testing.spells;

import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.ChargedSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChargedDevSpell extends ChargedSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {
        float chargeRatio = (float) simulacrumData.lifetime() / simulacrumData.maxLifetime();
        System.out.println("[ChargedDevSpell] ⚡ BOOM! ChargeRatio=" + chargeRatio
                + " Player=" + context.caster.getName().getString());
    }

    public ChargedDevSpell() {
        super();

        this.spellName = "ChargedDevSpell";
        this.manaCost = 30;
        this.cooldown = 60;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 100; // charge time / max lifetime
    }
    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? SpellValidator.CastFailureReason.OK : SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
