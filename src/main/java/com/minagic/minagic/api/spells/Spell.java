package com.minagic.minagic.api.spells;

import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;

// An abstract class representing a spell with casting lifecycle methods and validation.
public abstract class Spell {
    // properties
    protected int cooldown = 0;
    protected int manaCost = 0;
    protected int simulacraThreshold = 0;
    protected int simulacraMaxLifetime = -1;
    protected String spellName = "No Spell";
    protected boolean isTechnical = false;


    // CASTING LIFECYCLE METHODS

    public void applyCooldown(SpellCastContext context, int cooldown) {
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        cooldowns.setCooldown(ModSpells.getId(this), cooldown);
        context.caster.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get(), cooldowns);
    }

    public void drainMana(SpellCastContext context, int manaCost) {
        var mana = context.caster.getData(ModAttachments.MANA.get());
        mana.drainMana(manaCost);
        context.caster.setData(ModAttachments.MANA.get(), mana);
    }

    public void perform(SpellEventPhase phase, SpellCastContext context) {
        SpellValidationResult ctx_validation = context.validate();
        if (!ctx_validation.success()) {
            System.out.println("Performing "+ phase + " failed, REASON: " + ctx_validation.failureMessage());
            return;
        }
        SpellValidationResult before = before(phase, context);
        if (!before.success()) {
            System.out.println("Performing "+ phase + " failed, one or many prerequisites check failed: " + before.failureMessage());
            SpellValidator.showFailureIfNeeded(context, before);
            return;
        }

        switch (phase) {
            case START -> start(context);
            case STOP -> stop(context);
            case EXIT_SIMULACRUM -> exitSimulacrum(context);
            case CAST -> cast(context);
            case TICK -> tick(context);
        }

        after(phase, context);
    }

    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context) {
        return SpellValidationResult.OK;
    }

    protected void after(SpellEventPhase phase, SpellCastContext context) {}

    // OVERRIDES TO DEFINE SPELL BEHAVIOR
    // the main spell logic goes here
    // the context is guaranteed to be valid here
    protected void start(SpellCastContext context){}

    protected void tick(SpellCastContext context){}

    protected void stop(SpellCastContext context){}

    protected void cast(SpellCastContext context) {}

    protected void exitSimulacrum(SpellCastContext context){}

    public final String getString() {
        return spellName;
    }


    public final int getCooldownTicks() {
        return cooldown;
    }

    // post cast will drain this much mana from caster
    public final int getManaCost() {
        return manaCost;
    }

    public final boolean isTechnical() {return isTechnical;}

    // CASTER VALIDATION METHODS
    // check if caster can use this spell
    // default: OK for all casters
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        return SpellValidator.CastFailureReason.OK;
    }
    //  HUD
    public int color(float progress) {
        return 0x00000000;
    }

    // EQUALITY OVERRIDES

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

}
