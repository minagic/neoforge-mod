package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;


public abstract class Spell {

    // ENUM: reasons a spell cast might fail due to caster issues

    public enum CastFailureReason {
        CASTER_CLASS_MISMATCH,
        CASTER_SUBCLASS_MISMATCH,
        CASTER_CLASS_LEVEL_TOO_LOW,
        OK
    }

    // helper methods
    // ────────────────────────────────────────────────
// Helper: ensure caster exists and can use the spell
// ────────────────────────────────────────────────
    protected boolean validateCaster(SpellCastContext context) {
        if (context.caster == null) {
            return false; // no caster
        }

        CastFailureReason reason = canCast(context);
        if (reason != CastFailureReason.OK) {
            handleCastFailure(context, reason);
            return false;
        }

        return true;
    }

    // ────────────────────────────────────────────────
    // Helper: send HUD feedback for class/subclass/level issues
    // ────────────────────────────────────────────────
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

    // ────────────────────────────────────────────────
    // Helper: ensure spell cooldown is available
    // ────────────────────────────────────────────────
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
            return false;
        }
        return true;
    }

    // ────────────────────────────────────────────────
// Helper: ensure caster has sufficient mana
// ────────────────────────────────────────────────
    protected boolean validateMana(SpellCastContext context) {
        var mana = context.caster.getData(ModAttachments.MANA.get());
        if (mana.getMana() < getManaCost()) {
            HudAlertManager.addToEntity(
                    context.caster,
                    "Not enough mana to cast " + getString() + ".",
                    0xFF555500,
                    1,
                    20
            );
            return false;
        }
        return true;
    }

    // CASTING LIFECYCLE METHODS

    // returns the caster if all checks pass, null otherwise
    // spell.onCast calls this automatically
    public @Nullable LivingEntity preCast(SpellCastContext context,
                                             boolean checkCaster,
                                             boolean checkCooldown,
                                             boolean checkMana) {
        if (context.level.isClientSide()) {
            return null; // NEVER EVER CAST ON THE CLIENT
        }

        if (checkCaster && !validateCaster(context)) {
            return null;
        }

        if (checkCooldown && !validateCooldown(context)) {
            return null;
        }

        if (checkMana && !validateMana(context)) {
            return null;
        }

        return context.caster;
    }

    // called after spell casting logic to apply cooldowns and mana costs
    public void postCast(SpellCastContext context, boolean applyCooldown, boolean applyManaCost) {
        applyMagicCosts(context, applyCooldown, applyManaCost);
    }

    protected void applyMagicCosts(SpellCastContext context, boolean applyCooldown, boolean applyManaCost) {
        if (applyCooldown) {
            var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
            cooldowns.setCooldown(ModSpells.getId(this), getCooldownTicks());
            context.caster.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get(), cooldowns);
        }

        if (applyManaCost) {
            var mana = context.caster.getData(ModAttachments.MANA.get());
            mana.drainMana(getManaCost());
            context.caster.setData(ModAttachments.MANA.get(), mana);
        }
    }




    // this is called when spell casting item is used (RMB press)
    public void onStart(SpellCastContext context) {
        onCast(context);
    }

    // this is called every tick while in simulacrum / channeling spell slot
    public void tick(SpellCastContext context){
        // No-op by default
    }

    // this is called when spell casting item is released (RMB release)
    public void onStop(SpellCastContext context) {
        // No-op by default
    }

    // this is called to perform the actual spell casting logic (after pre-cast checks)
    // can be called directly from onStart, tick, or onStop as needed
    // WILL be called when simulacrum or channelling spell slot reaches its threshold
    public void onCast(SpellCastContext context) {
        LivingEntity caster = preCast(context, true, true, true);
        if (caster == null) {
            return; // Pre-cast checks failed
        }
        cast(new SpellCastContext(caster, context.level, context.stack)); // guarantee a living entity
        postCast(context, true, true);
    }



    // the main spell logic goes here
    // the context.caster is guaranteed to be non-null here
    public void cast(SpellCastContext context) {
        System.out.println("No spell");
    }





    // this returns a string name for this spell (for display / logging purposes)
    public String getString() {
        return "No Spell";
    }

    // MAGIC COST METHODS
    // post cast will set this many ticks of cooldown on the spell to the caster
    public int getCooldownTicks() {
        return 0;
    }

    // post cast will drain this much mana from caster
    public int getManaCost() {
        return 0;
    }

    // Simulacrum activation threshold
    // Simulacrum spell slot will call spell.onCast() when this many ticks have passed
    // repeatedly
    public int getSimulacrumThreshold(){
        return 0; // No simulacrum by default
    }

    // Simulacrum lifetime limit
    // Simulacrum spell slot will auto-expire after this many ticks
    public int getMaxLifetime(){
        return -1; // Infinite lifetime by default
    }

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
}
