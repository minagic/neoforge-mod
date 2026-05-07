package com.minagic.minagic.spells;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.AutoDetection;

@AutoDetection.Spell
public class NoneSpell extends Spell {
    public NoneSpell() {
        super(Spell.defaultProperties()
                .withIdName("none_spell"));
    }
}
