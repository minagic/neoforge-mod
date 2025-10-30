package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Spell {

    protected @Nullable LivingEntity preCast(SpellCastContext context) {
        if (context.level.isClientSide()){
            return null; // NEVER EVER CAST ON THE CLIENT
        }


        if (!canCast(context)) {
            HudAlertManager hudAlertManager = context.caster.getData(ModAttachments.HUD_ALERTS);
            hudAlertManager.addAlert(context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass().getUnknownSpellMessage()
                    , 0xFFAA33CC, 1, 60);
            context.caster.setData(ModAttachments.HUD_ALERTS, hudAlertManager);
            return null; // this class cannot cast shit
        }

        String prereqMessage = magicPrerequisitesHelper(context);
        if (!Objects.equals(prereqMessage, "")) {
            if (prereqMessage.contains("mana")) {
                HudAlertManager hudAlertManager = context.caster.getData(ModAttachments.HUD_ALERTS);
                hudAlertManager.addAlert(prereqMessage, 0xFF3399FF, 2, 60);
                context.caster.setData(ModAttachments.HUD_ALERTS, hudAlertManager);
            }
            else{
                HudAlertManager hudAlertManager = context.caster.getData(ModAttachments.HUD_ALERTS);
                hudAlertManager.addAlert(prereqMessage, 0xFFFFAA33, 3, 60);
                context.caster.setData(ModAttachments.HUD_ALERTS, hudAlertManager);
            }
            return null;
        }

        return context.caster;
    }

    public String magicPrerequisitesHelper(SpellCastContext context) {

        // set cooldown
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        var mana = context.caster.getData(ModAttachments.MANA.get());

        if (mana.getMana() < getManaCost()) {
            return "Not enough mana to cast " + getString() + ".";
        }

        if (cooldowns.getCooldown(ModSpells.getId(this)) > 0) {
            return "Spell " + getString() + " is on cooldown.";
        }

        return "";

    }

    public LivingEntity preCast(SpellCastContext context, boolean checkPrerequisites) {
        // Bypass prerequisite check
        if (checkPrerequisites) {
            return preCast(context);
        } else {
            if (!context.level.isClientSide()) {
                if(canCast(context)) {
                    return context.caster;
                }
            } else {
                return null;
            }
        }
        return null;
    }
    // CASTING
    public void onStart(SpellCastContext context) {
        cast(context);
    }

    public void onStop(SpellCastContext context) {
        // No-op by default
    }
    public void cast(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        System.out.println("No spell");

        applyMagicCosts(context);
    }

    public void applyMagicCosts(SpellCastContext context) {
        // set cooldown
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        var mana = context.caster.getData(ModAttachments.MANA.get());

        mana.drainMana(getManaCost());
        cooldowns.setCooldown(ModSpells.getId(this), getCooldownTicks());
    }

    public String getString() {
        return "No Spell";
    }

    public int getCooldownTicks() {
        return 0;
    }

    public int getManaCost() {
        return 0;
    }

    public int getSimulacrumThreshold(){
        return 0; // No simulacrum by default
    }

    public int getMaxLifetime(){
        return -1; // Infinite lifetime by default
    }

    public boolean canCast(SpellCastContext context) {
        return true;
    }

    public abstract void tick(SpellCastContext context);

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
