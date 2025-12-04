package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;

public class ChanneledAutonomousSpell extends Spell implements ISimulacrumSpell {
    public ChanneledAutonomousSpell() {
        super();

        this.spellName = "ChanneledAutonomousSpell";
        this.manaCost = 20;
        this.cooldown = 30;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = -1; // no max lifetime
    }

    // lifecycle like of channelled spell

    @Override
    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context) {
        SpellValidationResult result = SpellValidationResult.OK;

        switch (phase) {
            case START, STOP -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
            case CAST, TICK -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateMana(this, context, getManaCost()))
                        .and(SpellValidator.validateItem(this, context));
            }
            case EXIT_SIMULACRUM -> {
                result = result.and(SpellValidator.validateCaster(this, context));
            }
        }
        SpellValidator.showFailureIfNeeded(context, result);
        return result;
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context) {
        switch (phase) {
            case TICK -> drainMana(context, getManaCost());
            case EXIT_SIMULACRUM -> applyCooldown(context, getCooldownTicks());
            default -> {
            }
        }
    }

    @Override
    public void start(SpellCastContext context) {
        SimulacraAttachment.setChanneling(context.target, context, this, getSimulacrumThreshold(), -1);

    }

    @Override
    public void tick(SpellCastContext context) {
        // no-op
    }

    @Override
    public void stop(SpellCastContext context) {
        SimulacraAttachment.clearChanneling(context.target);
    }

    @Override
    public void exitSimulacrum(SpellCastContext context) {}


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
        return 0xFFAAAAFF;
    }


}
