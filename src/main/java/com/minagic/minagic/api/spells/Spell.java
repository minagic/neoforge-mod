package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.capabilities.SpellMetadata;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;

import java.util.List;

// An abstract class representing a spell with casting lifecycle methods and validation.
public abstract class Spell {
    // properties
    protected int cooldown = 0;
    protected int manaCost = 0;
    protected int simulacraThreshold = 0;
    protected int simulacraMaxLifetime = -1;
    protected String spellName = "No Spell";
    protected boolean isTechnical = false;

    // ENUM: reasons a spell cast might fail due to caster issues

    public enum CastFailureReason {
        CASTER_CLASS_MISMATCH,
        CASTER_SUBCLASS_MISMATCH,
        CASTER_CLASS_LEVEL_TOO_LOW,
        OK
    }

    //SpellEngine Integrity Protection
    //ALWAYS USE IN pre* CHECKS

    protected void handleCastFailure(SpellCastContext context, CastFailureReason reason) {
        var playerClass = context.caster.getData(ModAttachments.PLAYER_CLASS)
                .getMainClass();

        String message = switch (reason) {
            case CASTER_CLASS_MISMATCH -> playerClass.getUnknownSpellMessage();
            case CASTER_SUBCLASS_MISMATCH -> playerClass.getSubclassMismatchMessage();
            case CASTER_CLASS_LEVEL_TOO_LOW -> playerClass.getLevelTooLowMessage();
            default -> null;
        };

        if (message != null) {
            HudAlertManager.addToEntity(
                    context.caster,
                    message,
                    0xFF555500,
                    1,
                    20
            );
        }
    }

    protected boolean validateContext(SpellCastContext context) {
        if (context.caster == null) return false; // No caster


        if (context.level() == null) return false; // No level


        if (context.caster.asLivingEntity() == null) return false; // Caster must be a living entity

        if (context.target == null) return false; // No target

        if (context.target.asLivingEntity() == null) return false;

        if (!(context.target.isAlive() && context.caster.isAlive())) return false;

        return !context.level().isClientSide(); // NEVER EVER CAST ON THE CLIENT
    }

    protected boolean validateCaster(SpellCastContext context) {

        CastFailureReason reason = canCast(context);
        if (reason != CastFailureReason.OK) {
            handleCastFailure(context, reason);
            System.out.println("FAILURE REASON: " + reason);
            return false;
        }

        return true;
    }

    protected boolean validateCooldown(SpellCastContext context) {
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        if (cooldowns.getCooldown(ModSpells.getId(this)) > 0) {
            HudAlertManager.addToEntity(
                    context.caster,
                    "Spell is on cooldown!",
                    0xFF555500,
                    1,
                    20
            );
            System.out.println("FAILURE REASON: SPELL ON COOLDOWN");
            return false;
        }
        return true;
    }

    protected boolean validateMana(SpellCastContext context, int manaCost) {
        var mana = context.caster.getData(ModAttachments.MANA.get());
        if (mana.getMana() < manaCost) {
            HudAlertManager.addToEntity(
                    context.caster,
                    "Not enough mana to cast " + getString() + ".",
                    0xFF555500,
                    1,
                    20
            );
            System.out.println("FAILURE REASON: INSUFFICIENT MANA");
            return false;
        }
        return true;
    }

    protected boolean validateItem(SpellCastContext context){
        return true;
    }

    protected boolean validateSimulacrum(SpellCastContext context) {
        return !(context.simulacrtumLifetime == null || context.simulacrtumLifetime.remainingLifetime() == 0);
    }

    protected boolean validateMetadata(SpellCastContext context, List<String> keys) {
        for (String key : keys) {
            if (!SpellMetadata.has(context.target, this, key)) return false;
        }
        return true;
    }

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
        if (!validateContext(context)) {
            System.out.println("Performing "+ phase + " failed, invalid context");
            return;
        }
        if (!before(phase, context)) {
            System.out.println("Performing "+ phase + " failed, one or many prerequisites check failed");
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

    protected boolean before(SpellEventPhase phase, SpellCastContext context) {
        return true;
    }

    protected void after(SpellEventPhase phase, SpellCastContext context) {}


    // OVERRIDES TO DEFINE SPELL BEHAVIOR

    // the main spell logic goes here
    // the context.caster is guaranteed to be non-null here

    protected void start(SpellCastContext context){}

    protected void tick(SpellCastContext context){}

    protected void stop(SpellCastContext context){}

    protected void cast(SpellCastContext context) {}

    protected void exitSimulacrum(SpellCastContext context){}


    // this returns a string name for this spell (for display / logging purposes)
    public final String getString() {
        return spellName;
    }

    // MAGIC COST METHODS
    // post cast will set this many ticks of cooldown on the spell to the caster
    public final int getCooldownTicks() {
        return cooldown;
    }

    // post cast will drain this much mana from caster
    public final int getManaCost() {
        return manaCost;
    }

    // Simulacrum activation threshold
    // Simulacrum spell slot will call spell.perform(CAST, ...) when this many ticks have passed
    // repeatedly
    public final int getSimulacrumThreshold(){return simulacraThreshold;}

    // Simulacrum lifetime limit
    // Simulacrum spell slot will auto-expire after this many ticks
    public int getMaxLifetime(){return simulacraMaxLifetime;}

    public final boolean isTechnical() {return isTechnical;}

    // CASTER VALIDATION METHODS
    // check if caster can use this spell
    // default: OK for all casters
    public CastFailureReason canCast(SpellCastContext context) {
        return CastFailureReason.OK;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    // HUD
    public float progress(SimulacrumSpellData data) {
        return 0f;
    }

    public int color(float progress) {
        return 0x00000000;
    }
    protected SpellCastContext inverted(SpellCastContext ctx) {
        return new SpellCastContext(
                ctx.target,
                ctx.caster
        );
    }
}
