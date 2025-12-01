package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChargedSpell extends Spell {
    public ChargedSpell() {
        super(); // keep whatever superclass initialization you rely on

        this.manaCost = 0;               // default for charged spells
        this.cooldown = 0;               // stays default unless you override elsewhere
        this.spellName = "Charged Spell";
        this.simulacraThreshold = 0;     // cannot be autocast
        this.simulacraMaxLifetime = 0;   // default max lifetime for charged spells
    }


    @Override
    protected boolean before(SpellEventPhase phase, SpellCastContext context) {
        return switch (phase) {
            case START -> validateCaster(context) && validateCooldown(context) && validateItem(context);
            case TICK -> validateCaster(context) && validateItem(context) && validateSimulacrum(context);
            case STOP, EXIT_SIMULACRUM -> validateCaster(context) && validateItem(context);
            case CAST -> validateCaster(context) && validateItem(context) && validateCooldown(context) && validateMana(context, getManaCost()) && validateSimulacrum(context);
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

    // lifecycle methods
    @Override
    public final void start(SpellCastContext context) {
        SimulacraAttachment.setActiveChanneling(
                context,
                this,
                0,
                getMaxLifetime()
        );
    }

    @Override
    public void tick(SpellCastContext context) {
        //System.out.println("Charging spell: " + getString() + " | Charge time: " + chargeTime);
        //chargeTime = context.simulacrtumLifetime.lifetime();
    }

    @Override
    public final void stop(SpellCastContext context) {

        SimulacraAttachment.clearChanneling(
                context.target
        );


    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {
        perform(SpellEventPhase.CAST, context);
    }

    // HUD
    @Override
    public final float progress(SimulacrumSpellData data) {
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
