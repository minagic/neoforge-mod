package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;

public class InvertedAutonomousSpell extends AutonomousSpell {
    @Override
    public void onCast(SpellCastContext context) {
        super.onCast(inverted(context));
    }

    @Override
    public void onStart(SpellCastContext context) {
        super.onStart(inverted(context));
    }

    @Override
    public void onTick(SpellCastContext context) {
        super.onTick(inverted(context));
    }

    @Override
    public void onStop(SpellCastContext context) {
        super.onStop(inverted(context));
    }

    @Override
    public void onExitSimulacrum(SpellCastContext context) {
        super.onExitSimulacrum(inverted(context));
    }
}
