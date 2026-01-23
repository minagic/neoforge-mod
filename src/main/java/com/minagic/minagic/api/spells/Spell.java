package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;
import org.jetbrains.annotations.Nullable;

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

    public void perform(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData) {
        SpellValidationResult ctx_validation = context.validate();
        if (!ctx_validation.success()) {
            System.out.println("Performing "+ phase + " failed, REASON: " + ctx_validation.failureMessage());
            return;
        }
        SpellValidationResult before = before(phase, context, simulacrumData);
        if (!before.success()) {
            System.out.println("Performing "+ phase + " failed, one or many prerequisites check failed: " + before.failureMessage());
            SpellValidator.showFailureIfNeeded(context, before);
            return;
        }

        if (!SpellValidator.validateSimulacrum(simulacrumData).success() && this instanceof ISimulacrumSpell) {
            System.out.println("WARNING: ISimulacrumSpell "+ this.getString() + "'s PHASE" + phase + " is used without a valid SimulacrumData object");
        }

        switch (phase) {
            case START -> start(context, simulacrumData);
            case STOP -> stop(context, simulacrumData);
            case EXIT_SIMULACRUM -> exitSimulacrum(context, simulacrumData);
            case CAST -> cast(context, simulacrumData);
            case TICK -> tick(context, simulacrumData);
        }

        after(phase, context, simulacrumData);
    }

    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData) {
        return SpellValidationResult.OK;
    }

    protected void after(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData) {}

    // OVERRIDES TO DEFINE SPELL BEHAVIOR
    // the main spell logic goes here
    // the context is guaranteed to be valid here
    protected void start(SpellCastContext context, SimulacrumSpellData simulacrumData){}

    protected void tick(SpellCastContext context, SimulacrumSpellData simulacrumData){}

    protected void stop(SpellCastContext context, SimulacrumSpellData simulacrumData){}

    protected void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {}

    protected void exitSimulacrum(SpellCastContext context, SimulacrumSpellData simulacrumData){}

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
