package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;
import org.jetbrains.annotations.Nullable;


/// An abstract class representing spells that take effect immediately upon casting.
/// To use, extend this class and implement the cast method, as well as getManaCost, getCooldownTicks and getString.
public class InstanteneousSpell extends Spell{
    public InstanteneousSpell() {
        super();

        this.spellName = "InstantaneousSpell";
        this.manaCost = 0;
        this.cooldown = 0;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 0;
    }



    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData){
        perform(SpellEventPhase.CAST, context, null);
    }

    @Override
    public final void tick(SpellCastContext context, SimulacrumData simulacrumData){
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData){
        // no-op
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData){
        // no-op
    }

    @Override
    public final int color(float progress){
        return 0x00000000; // transparent
    }

}
