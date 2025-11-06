package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;


/// An abstract class representing spells that take effect immediately upon casting.
/// To use, extend this class and implement the cast method, as well as getManaCost, getCooldownTicks and getString.
public class InstanteneousSpell extends Spell{

    @Override
    public final int getMaxLifetime(){
        return 0; // instantaneous spells have no simulacra lifetime
    }

    @Override
    public final int getSimulacrumThreshold(){return 0; // no simulacrum spell slots allowed
    }

    @Override
    public int getCooldownTicks(){
        return 0; // default cooldown
    }

    @Override
    public String getString() {
        return "Instantaneous Spell";
    }

    @Override
    public int getManaCost() {
        return 0; // default mana cost
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
        return checkContext(context, true, true, getManaCost(), true, false);
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
        applyMagicCosts(context, getCooldownTicks(), getManaCost());
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

}
