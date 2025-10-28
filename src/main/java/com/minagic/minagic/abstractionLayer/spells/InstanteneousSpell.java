package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;


/// An abstract class representing spells that take effect immediately upon casting.
/// To use, extend this class and implement the cast method, as well as getManaCost, getCooldownTicks and getString.
public class InstanteneousSpell extends Spell{

    @Override
    public final int getMaxLifetime(){
        return 0; // instantaneous spells have no lifetime
    }

    @Override
    public final int getSimulacrumThreshold(){
        return 0; // no simulacrum spellslots
    }

    @Override
    public int getCooldownTicks(){
        return 1; // default cooldown
    }

    @Override
    public String getString() {
        return "Instantaneous Spell";
    }

    @Override
    public int getManaCost() {
        return 5; // default mana cost
    }

    // lifecycle methods
    @Override
    public final void onStart(SpellCastContext context) {
        cast(context);
    }

    @Override
    public final void onStop(SpellCastContext context) {
        // No-op for instantaneous spells
    }

    @Override
    public final void tick(SpellCastContext context) {
        // No-op for instantaneous spells
    }
}
