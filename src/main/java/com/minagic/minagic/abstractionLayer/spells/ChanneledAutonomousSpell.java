package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
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
            case START -> validateCaster(context) && validateCooldown(context) && validateItem(context);
            case STOP -> validateCaster(context) && validateCooldown(context) && validateItem(context);
            case CAST -> validateCaster(context) && validateCooldown(context) && validateMana(context, getManaCost()) && validateItem(context);
            case TICK, EXIT_SIMULACRUM -> false;
        };
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context) {
        switch (phase) {
            case CAST -> drainMana(context, getManaCost());
            case EXIT_SIMULACRUM -> applyCooldown(context, getCooldownTicks());
            default -> {
            }
        }
    }

    @Override
    public final void start(SpellCastContext context) {
        PlayerSimulacraAttachment.setActiveChanneling(context, this, getSimulacrumThreshold(), -1);

    }

    @Override
    public final void tick(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context) {
        PlayerSimulacraAttachment.clearChanneling(context.target);

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
        return 0xFFAAAAFF;
    }


}
