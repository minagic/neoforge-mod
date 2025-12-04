package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;

public class ChanneledSpell extends Spell implements ISimulacrumSpell {
    public ChanneledSpell() {
        super();

        this.spellName = "ChanneledAutonomousSpell";
        this.manaCost = 20;
        this.cooldown = 30;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = -1; // no max lifetime
    }

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
            case STOP, EXIT_SIMULACRUM -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateItem(this, context))
                        .and(SpellValidator.validateSimulacrum(context));
            }
            case CAST -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateMana(this, context, getManaCost()))
                        .and(SpellValidator.validateItem(this, context))
                        .and(SpellValidator.validateSimulacrum(context));
            }
            case TICK -> {
                result = result.and(SpellValidationResult.INVALID_PHASE);
            }
        }
        return result;
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context) {
        switch (phase) {
            case CAST -> {
                applyCooldown(context, getCooldownTicks());
                drainMana(context, getManaCost());
                SimulacraAttachment.clearChanneling(context.target);
            }
            case EXIT_SIMULACRUM -> applyCooldown(context, getCooldownTicks());
            default -> {
            }
        }
    }

    // Lifecycle methods


    @Override
    public final void start(SpellCastContext context) {
        SimulacraAttachment.setChanneling(
                context.target,
                context,
                this,
                getSimulacrumThreshold(),
                -1);
    }

    @Override
    public final void tick(SpellCastContext context) {
        // no-op for channeled spells
    }
    @Override
    public final void stop(SpellCastContext context) {
        SimulacraAttachment.clearChanneling(context.target);
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {
        // no-op for channeled spells
    }


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
        return data.lifetime()/data.threshold();
    }

    @Override
    public final int color(float progress) {
        return 0xFFFF00FF; // magenta for channeled spells
    }

}
