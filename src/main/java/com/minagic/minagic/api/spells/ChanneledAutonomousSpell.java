package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChanneledAutonomousSpell extends Spell {
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
    protected boolean before(SpellEventPhase phase, SpellCastContext context) {
        return switch (phase) {
            case START, STOP -> validateCaster(context) && validateCooldown(context) && validateItem(context);
            case CAST, TICK -> validateCaster(context) && validateCooldown(context) && validateMana(context, getManaCost()) && validateItem(context);
            case EXIT_SIMULACRUM -> validateCaster(context);
        };
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
        SimulacraAttachment.setActiveChanneling(context, this, getSimulacrumThreshold(), -1);

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
