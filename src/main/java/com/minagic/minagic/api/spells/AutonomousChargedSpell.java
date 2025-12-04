package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;
import org.jetbrains.annotations.Nullable;

//// An abstract class representing spells that are charged up over time before being released.
public class AutonomousChargedSpell extends Spell implements ISimulacrumSpell {
    public AutonomousChargedSpell() {
        super();

        this.spellName = "AutonomousChargedSpell";
        this.manaCost = 0;
        this.cooldown = 0;

        // Lifetime equals threshold in original behavior:
        // maxLifetime = simulacrumThreshold, but since you initialize by constructor,
        // we set both here.
        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 0;
    }

    @Override
    public int getSimulacrumThreshold() {
        return simulacraThreshold;
    }

    @Override
    public final int getSimulacrumMaxLifetime() {
        return getSimulacrumThreshold();
    }

    @Override
    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData) {
        SpellValidationResult result = SpellValidationResult.OK;
        switch (phase) {
            case START -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
            case CAST -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateMana(this, context, getManaCost()))
                        .and(SpellValidator.validateItem(this, context));
            }
            case TICK, STOP, EXIT_SIMULACRUM -> {
                result = result.and(SpellValidationResult.INVALID_PHASE);
            }
        }
        SpellValidator.showFailureIfNeeded(context, result);
        return result;
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData) {
        if (phase == SpellEventPhase.CAST) {
            applyCooldown(context, getManaCost());
            drainMana(context, getManaCost());
        }
    }



    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData) {

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
    public  final void tick(SpellCastContext context, SimulacrumSpellData simulacrumData) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumSpellData simulacrumData) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumSpellData simulacrumData) {
        // no-op
    }


    @Override
    public final float progress(SimulacrumSpellData data) {
        return data.remainingLifetime() / data.maxLifetime();
    }

    @Override
    public final int color(float progress) {
        return 0xFFFFFFAA;
    }


}
