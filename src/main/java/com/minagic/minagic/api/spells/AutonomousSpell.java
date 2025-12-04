package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;

/**
 * A simple self-managed spell that automatically attaches or detaches itself
 * as a background simulacrum when cast. Runs indefinitely (maxLifetime = -1)
 * until toggled again.
 */
public class AutonomousSpell extends Spell implements ISimulacrumSpell {

    @Override
    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context) {
        SpellValidationResult result = SpellValidationResult.OK;

        switch (phase) {
            case START -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
            case TICK, CAST -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateMana(this, context, getManaCost()))
                        .and(SpellValidator.validateItem(this, context));
            }
            case EXIT_SIMULACRUM -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
        }
        SpellValidator.showFailureIfNeeded(context, result);
        return result;
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context) {
        switch (phase) {
            case CAST -> drainMana(context, getManaCost());
            case EXIT_SIMULACRUM -> applyCooldown(context, getManaCost());
            default -> {
            }
        }
    }


    @Override
    public void start(SpellCastContext context) {
        // Get player simulacra attachment
        SimulacraAttachment sim = context.target.getData(ModAttachments.PLAYER_SIMULACRA.get());

        // Toggle logic: if already active, remove; else add
        boolean existing = sim.hasSpell(ModSpells.getId(this));

        if (existing) {
            SimulacraAttachment.removeSimulacrum(context.target, ModSpells.getId(this));
        } else {
            SimulacraAttachment.addSimulacrum(context.target, context, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
        }

    }

    @Override
    public void tick(SpellCastContext context) {
        // No-op for autonomous spells
    }

    @Override
    public final void stop(SpellCastContext context) {
        // No-op for autonomous spells
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {}

    @Override
    public int getSimulacrumThreshold() {
        return this.simulacraThreshold;
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return this.simulacraMaxLifetime;
    }

    @Override
    public final float progress(SimulacrumSpellData data) {
        if (data.maxLifetime() <= 0) {
            return 1f;
        }
        return data.remainingLifetime() / data.maxLifetime();
    }

    @Override
    public final int color(float progress) {
        return 0xFFAAFFAA;
    }

}
