package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;

@AutoDetection.Spell
public class StellarRain extends AutonomousSpell {

    public StellarRain() {
        this.spellName = "Stellar Rain";
        this.idName = "stellar_rain";
        this.cooldown = 0;
    }

    @Override
    public void cast(SpellCastContext context, @org.jetbrains.annotations.Nullable SimulacrumData simulacrumData) {
        // TODO: CAST LOGIC
    }

    @Override
    public void tick(SpellCastContext context, @org.jetbrains.annotations.Nullable SimulacrumData simulacrumData) {
        // TODO: TICK LOGIC
    }
}
