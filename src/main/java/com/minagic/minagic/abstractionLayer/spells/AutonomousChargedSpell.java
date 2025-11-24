package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;

//// An abstract class representing spells that are charged up over time before being released.
public class AutonomousChargedSpell extends Spell {
    public AutonomousChargedSpell() {
        super();

        this.spellName = "AutonomousChargedSpell";
        this.manaCost = 0;
        this.cooldown = 0;

        // Lifetime equals threshold in original behavior:
        // maxLifetime = simulacrumThreshold, but since you initialize by constructor,
        // we set both here.
        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = this.simulacraThreshold;
    }

    @Override
    public final int getMaxLifetime() {
        return getSimulacrumThreshold();
    }

    @Override
    protected boolean before(SpellEventPhase phase, SpellCastContext context) {
        return switch (phase) {
            case START -> validateCaster(context) && validateCooldown(context) && validateItem(context);
            case CAST -> validateCaster(context) && validateCooldown(context) && validateMana(context, getManaCost()) && validateItem(context);
            case TICK, STOP, EXIT_SIMULACRUM -> false;
        };
    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context) {
        if (phase == SpellEventPhase.CAST) {
            applyCooldown(context, getManaCost());
            drainMana(context, getManaCost());
        }
    }



    // lifecycle methods
    @Override
    public final void start(SpellCastContext context) {

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
    public  final void tick(SpellCastContext context) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void stop(SpellCastContext context) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {
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
