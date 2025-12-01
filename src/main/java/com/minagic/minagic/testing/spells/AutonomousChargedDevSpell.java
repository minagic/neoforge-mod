package com.minagic.minagic.testing.spells;
import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.AutonomousChargedSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class AutonomousChargedDevSpell extends AutonomousChargedSpell {

    @Override
    public void cast(SpellCastContext context) {
        System.out.println("[AutonomousChargedDevSpell] 💥 Detonated after delay! "
                + context.caster.getName().getString());

    }

    public AutonomousChargedDevSpell() {
        super();

        this.spellName = "AutonomousChargedDevSpell";
        this.manaCost = 15;
        this.cooldown = 100;

        this.simulacraThreshold = 60;
        this.simulacraMaxLifetime = 60; // autonomous charged invariant
    }

    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? CastFailureReason.OK : CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}