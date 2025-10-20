package com.minagic.minagic.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;

public interface ISpell {
    default boolean cast(SpellCastContext context) {
        return true;
        // return true if cast was successful
    }

    default int getCooldownTicks() {
        return 0;
    }
}
