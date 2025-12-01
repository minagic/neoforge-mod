package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChanneledSpell extends Spell {
    public ChanneledSpell() {
        super();

        this.spellName = "ChanneledAutonomousSpell";
        this.manaCost = 20;
        this.cooldown = 30;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = -1; // no max lifetime
    }

    @Override
    protected boolean before(SpellEventPhase phase, SpellCastContext context) {
        return switch (phase) {
            case START -> validateCaster(context) && validateCooldown(context) && validateItem(context);
            case STOP, EXIT_SIMULACRUM -> validateCaster(context) && validateItem(context);
            case CAST -> validateCaster(context) && validateCooldown(context) && validateMana(context, getManaCost()) && validateItem(context);
            case TICK -> false;
        };
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
    public final float progress(SimulacrumSpellData data) {
        return data.lifetime()/data.threshold();
    }

    @Override
    public final int color(float progress) {
        return 0xFFFF00FF; // magenta for channeled spells
    }

}
