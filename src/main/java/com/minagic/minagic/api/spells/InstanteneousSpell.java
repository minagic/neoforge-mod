package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
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

    @Override
    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData){
        SpellValidationResult result = SpellValidationResult.OK;

        switch (phase) {
            case START -> {
                return result;
            }
            case CAST -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateItem(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateMana(this, context, getManaCost()));
            }
            case STOP, EXIT_SIMULACRUM, TICK -> {
                return SpellValidationResult.INVALID_PHASE;
            }
        }


        return result;
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData){
        if (phase == SpellEventPhase.CAST) {
            applyCooldown(context, getCooldownTicks());
            drainMana(context, getManaCost());
        }
    }

    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData){
        perform(SpellEventPhase.CAST, context, null);
    }

    @Override
    public final void tick(SpellCastContext context, SimulacrumSpellData simulacrumData){
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumSpellData simulacrumData){
        // no-op
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumSpellData simulacrumData){
        // no-op
    }

    @Override
    public final int color(float progress){
        return 0x00000000; // transparent
    }

}
