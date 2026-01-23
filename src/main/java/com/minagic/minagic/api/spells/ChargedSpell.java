package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;
import org.jetbrains.annotations.Nullable;

public class ChargedSpell extends Spell implements ISimulacrumSpell {
    public ChargedSpell() {
        super(); // keep whatever superclass initialization you rely on

        this.manaCost = 0;               // default for charged spells
        this.cooldown = 0;               // stays default unless you override elsewhere
        this.spellName = "Charged Spell";
        this.simulacraThreshold = 0;     // cannot be autocast
        this.simulacraMaxLifetime = 0;   // default max lifetime for charged spells
    }


    @Override
    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellValidationResult result = SpellValidationResult.OK;

        switch (phase) {
            case START -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
            case TICK -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateItem(this, context))
                        .and(SpellValidator.validateSimulacrum(simulacrumData));
            }
            case STOP, EXIT_SIMULACRUM -> {
                result = result.and(SpellValidator.validateCaster(this, context));
            }
            case CAST -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateItem(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateMana(this, context, getManaCost()))
                        .and(SpellValidator.validateSimulacrum(simulacrumData));
            }
        }
        return result;
    }

    private void after(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
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

    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SimulacraAttachment.setChanneling(
                context.target,
                context,
                this,
                0,
                getSimulacrumMaxLifetime()
        );
    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        SimulacraAttachment.clearChanneling(
                context.target
        );
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        perform(SpellEventPhase.CAST, context, simulacrumData);
    }

    @Override
    public int getSimulacrumThreshold() {
        return this.simulacraThreshold;
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return this.simulacraMaxLifetime;
    }

    // HUD
    @Override
    public final float progress(SimulacrumData data) {
        return data.lifetime() / Math.max(1, data.maxLifetime() );
    }

    @Override
    public final int color(float progress) {
        if (progress >= 0.8) {
            return 0xFFFF0000 ; // Red when approaching limit
        } else {
            return 0xFF0000FF; // Blue when charging
        }
    }

}
