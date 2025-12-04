package com.minagic.minagic.testing.spells;


import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.ChanneledSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChanneledDevSpell extends ChanneledSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {

        System.out.println("[ChanneledDevSpell] 🔫 Pew! " + context.caster.getName().getString());
    }

    public ChanneledDevSpell() {
        super();

        this.spellName = "ChanneledDevSpell";
        this.manaCost = 40;
        this.cooldown = 10;

        this.simulacraThreshold = 5;       // fire every 5 ticks
        this.simulacraMaxLifetime = -1;    // channeled = infinite lifetime
    }
    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? SpellValidator.CastFailureReason.OK : SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
