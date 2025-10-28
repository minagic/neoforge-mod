package com.minagic.minagic.spells;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class NoneSpell extends Spell {
    @Override
    public String getString() {
        return "NoneSpell";
    }

    @Override
    public void tick(SpellCastContext context) {
        // No operation
    }
}
