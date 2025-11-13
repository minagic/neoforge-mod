package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;


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

    // pre* methods
    @Override
    public final boolean preStart(SpellCastContext context){
       return true;
    }

    @Override
    public final boolean preTick(SpellCastContext context){
        return false; // no-op
    }

    @Override
    public final boolean preStop(SpellCastContext context){
        return false; // no-op
    }

    @Override
    public final boolean preExitSimulacrum(SpellCastContext context){
        return false; // no-op
    }

    @Override
    public final boolean preCast(SpellCastContext context){
        return validateContext(context) &&
                validateCaster(context) &&
                validateItem(context) &&
                validateCooldown(context) &&
                validateMana(context, getManaCost());
    }

    // post* methods
    @Override
    public final void postStart(SpellCastContext context){
        // no-op
    }
    @Override
    public final void postTick(SpellCastContext context){
        // no-op
    }

    @Override
    public final void postStop(SpellCastContext context){
        // no-op
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context){
        // no-op
    }

    @Override
    public final void postCast(SpellCastContext context){
        applyCooldown(context, getCooldownTicks());
        drainMana(context, getManaCost());
    }

    // lifecycle methods
    @Override
    public final void start(SpellCastContext context){
        onCast(context);
    }

    @Override
    public final void tick(SpellCastContext context){
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context){
        // no-op
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context){
        // no-op
    }

    @Override
    public final float progress(SimulacrumSpellData data){
        return 0f; // instantaneous spells have no progress
    }

    @Override
    public final int color(float progress){
        return 0x00000000; // transparent
    }

}
