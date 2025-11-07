package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;

// An abstract class representing a spell with casting lifecycle methods and validation.
// on* methods are called by the spellcasting system
// pre* and post* methods are called automatically by the on* methods
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
            return false;
        }
        return true;
    }

    protected boolean validateItem(SpellCastContext context){
        if (context.stack == null || context.stack.isEmpty()) {
            HudAlertManager.addToEntity(
                    context.caster,
                    "Item to cast spell not found",
                    0xFF555500,
                    1,
                    20
            );
            return false; // no spellcasting item
        }

        if (!(context.stack.getItem() instanceof SpellcastingItem<?>)) {
            HudAlertManager.addToEntity(
                    context.caster,
                    "Item cannot perform spell casting",
                    0xFF555500,
                    1,
                    20
            );
            return false; // item cannot perform spell casting
        }
        return true;
    }

    // CASTING LIFECYCLE METHODS

    // returns the caster if all checks pass, null otherwise
    // pre* methods, called by on* methods before * methods
    public boolean preCast(SpellCastContext context) {
        return false;
    }

    public boolean preExitSimulacrum(SpellCastContext context){
        return false;
    }

    public boolean preTick(SpellCastContext context) {
        return false;
    }

    public boolean preStart(SpellCastContext context) {
        return false;
    }

    public boolean preStop(SpellCastContext context) {
        return false;
    }

    // pre* helper
    protected boolean checkContext(SpellCastContext context,
                                        boolean checkCaster,
                                        boolean checkCooldown,
                                        int checkMana,
                                        boolean checkSpellcastingItem,
                                        boolean checkSimulacraLifetime) {
        if (context.caster == null) return false; // No caster


        if (context.level() == null) return false; // No level


        if (context.caster.asLivingEntity() == null) return false; // Caster must be a living entity


        if (context.target == null) return false; // No target


        if (context.target.asLivingEntity() == null) return false;

        if (!(context.target.isAlive() && context.caster.isAlive())) return false;



        if (context.level().isClientSide()) {
            return false; // NEVER EVER CAST ON THE CLIENT
        }

        if (checkSpellcastingItem && !validateItem(context)) {
            return false;
        }

        if (checkCaster && !validateCaster(context)) {
            return false;
        }

        if (checkCooldown && !validateCooldown(context)) {
            return false;
        }

        if (checkSimulacraLifetime && (context.simulacrtumLifetime == null || context.simulacrtumLifetime.remainingLifetime() == 0)) {
            return false;
        }

        return checkMana <= 0 || validateMana(context, checkMana);
    }


    // post* methods, called by on* methods after * methods

    public void postStart(SpellCastContext context) {}

    public void postTick(SpellCastContext context) {}

    public void postStop(SpellCastContext context) {}

    public void postCast(SpellCastContext context) {}

    public void postExitSimulacrum(SpellCastContext context) {}

    // post* helper to apply cooldowns and mana costs
    protected void applyMagicCosts(SpellCastContext context, int applyCooldown, int applyManaCost) {
        if (applyCooldown > 0) {
            var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
            cooldowns.setCooldown(ModSpells.getId(this), applyCooldown);
            context.caster.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get(), cooldowns);
        }

        if (applyManaCost > 0) {
            var mana = context.caster.getData(ModAttachments.MANA.get());
            mana.drainMana(applyManaCost);
            context.caster.setData(ModAttachments.MANA.get(), mana);
        }
    }


    // on* methods, called by the spellcasting system to drive the spell lifecycle
    // this is called when spell casting item is used (RMB press)
    public void onStart(SpellCastContext context) {
        if(!preStart(context)) return;
        start(context);
        postStart(context);
    }

    // this is called every tick while in simulacrum / channeling spell slot
    public void onTick(SpellCastContext context){
        if (!preTick(context)) return;
        tick(context);
        postTick(context);
    }

    // this is called when spell casting item is released (RMB release)
    public void onStop(SpellCastContext context) {
        if (!preStop(context)) return;
        stop(context);
        postStop(context);
    }

    // this is called to perform the actual spell casting logic (after pre-cast checks)
    // can be called directly from onStart, tick, or onStop as needed
    // WILL be called when simulacrum or channelling spell slot reaches its threshold
    public void onCast(SpellCastContext context) {
        if (!preCast(context)) return;
        cast(context);
        postCast(context);
    }


    // this is called when simulacrum spell slot is exited (lifetime exceeded, or manually removed)
    public void onExitSimulacrum(SpellCastContext context){
        System.out.println("onExitSimulacrum called for spell: " + getString());
        if (!preExitSimulacrum(context)) return;
        System.out.println("onExitSimulacrum called for spell: " + getString());
        exitSimulacrum(context);
        postExitSimulacrum(context);
    }


    // OVERRIDES TO DEFINE SPELL BEHAVIOR

    // the main spell logic goes here
    // the context.caster is guaranteed to be non-null here

    protected void start(SpellCastContext context){}

    protected void tick(SpellCastContext context){}

    protected void stop(SpellCastContext context){}

    protected void cast(SpellCastContext context) {}

    protected void exitSimulacrum(SpellCastContext context){}


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

    // HUD
    public float progress(SimulacrumSpellData data) {
        return 0f;
    }

    public int color(float progress) {
        return 0x00000000;
    }
}
