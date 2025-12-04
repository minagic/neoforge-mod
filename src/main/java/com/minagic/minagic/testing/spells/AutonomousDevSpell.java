package com.minagic.minagic.testing.spells;
import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class AutonomousDevSpell extends AutonomousSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {

        System.out.println("[AutonomousDevSpell] 🔄 Tick fired for "
                + context.caster.getName().getString());

    }

    public AutonomousDevSpell() {
        super();

        this.spellName = "AutonomousDevSpell";
        this.manaCost = 40;
        this.cooldown = 10;

        this.simulacraThreshold = 20;
        this.simulacraMaxLifetime = -1; // autonomous invariant
    }

    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? SpellValidator.CastFailureReason.OK : SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
