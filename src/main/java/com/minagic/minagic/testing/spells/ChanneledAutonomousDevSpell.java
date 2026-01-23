package com.minagic.minagic.testing.spells;


import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChanneledAutonomousDevSpell extends ChanneledAutonomousSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {

        System.out.println("[ChanneledAutonomousDevSpell] 🔫 Pew Pew Pew! lifetime="
                + simulacrumData+ " Player=" + context.caster.getName().getString());

    }

    public ChanneledAutonomousDevSpell() {
        super();

        this.spellName = "ChanneledAutonomousDevSpell";
        this.manaCost = 2;

        this.simulacraThreshold = 5;       // once every 0.5 seconds
        this.simulacraMaxLifetime = -1;    // channeled = infinite lifetime
    }
    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? SpellValidator.CastFailureReason.OK : SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
