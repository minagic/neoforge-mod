package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;

/**
 * A simple self-managed spell that automatically attaches or detaches itself
 * as a background simulacrum when cast. Runs indefinitely (maxLifetime = -1)
 * until toggled again.
 */
public class AutonomousSpell extends Spell {

    @Override
    public final boolean preStart(SpellCastContext context) {
        return validateContext(context) &&
                validateCaster(context) &&
                validateCooldown(context) &&
                validateItem(context);
    }

    @Override
    public boolean preTick(SpellCastContext context) {return false; }

    @Override
    public final boolean preStop(SpellCastContext context) {return false; }

    @Override
    public boolean preCast(SpellCastContext context) {
        return validateContext(context) &&
                validateCaster(context) &&
                validateCooldown(context) &&
                validateMana(context, getManaCost()) &&
                validateItem(context);
    }

    @Override
    public final boolean preExitSimulacrum(SpellCastContext context) {
        return validateContext(context) &&
                validateCaster(context) &&
                validateItem(context); }

    @Override
    public final void postStart(SpellCastContext context) {}

    @Override
    public final void postTick(SpellCastContext context) {}

    @Override
    public final void postStop(SpellCastContext context) {}

    @Override
    public void postCast(SpellCastContext context) {

        drainMana(context, getManaCost());
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context) {
        System.out.println("AutonomousSpell.postExitSimulacrum called");
        applyCooldown(context, getManaCost());
    }


    @Override
    public void start(SpellCastContext context) {
        // Get player simulacra attachment
        PlayerSimulacraAttachment sim = context.target.getData(ModAttachments.PLAYER_SIMULACRA.get());

        // Toggle logic: if already active, remove; else add
        var existing = sim.getBackgroundSimulacra().get(ModSpells.getId(this));

        if (existing != null) {
            PlayerSimulacraAttachment.removeSimulacrum(context.target, ModSpells.getId(this));
        } else {
            PlayerSimulacraAttachment.addSimulacrum(context, this, getSimulacrumThreshold(), getMaxLifetime());
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
