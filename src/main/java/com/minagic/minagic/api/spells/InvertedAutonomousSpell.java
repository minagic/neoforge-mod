package com.minagic.minagic.api.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;

public class InvertedAutonomousSpell extends AutonomousSpell {
    @Override
public void perform(SpellEventPhase phase, SpellCastContext context) {
    super.perform(phase, inverted(context));
}
}
