package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChargedSpell extends Spell {
    private int chargeTime = 0;

    protected final int getChargeTime() {
        return chargeTime;
    }

    @Override
    public int getMaxLifetime() {
        return 100; // Default max lifetime for charged spells
    }

    @Override
    public final int getSimulacrumThreshold() {
        return 0; // Charged spells cannot be automatically cast
    }

    @Override
    public String getString() {
        return "Charged Spell";
    }

    @Override
    public int getManaCost() {
        return 10; // Default mana cost for charged spells
    }

    // Lifecycle methods
    @Override
    public void onStart(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        PlayerSimulacraAttachment.setActiveChanneling(context.caster, context.level, this, getSimulacrumThreshold(), -1, context.stack);
    }

    @Override
    public void tick(SpellCastContext context) {
        chargeTime = context.simulacrtumLifetime == -1 ? chargeTime + 1 : context.simulacrtumLifetime;
    }

    @Override
    public final void onStop(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        PlayerSimulacraAttachment.clearChanneling(context.caster, context.level);
    }

    @Override
    public void exitSimulacrum(SpellCastContext context) {
        onCast(context);

    }

    // implement pre and post methods
    @Override
    public LivingEntity preCast(SpellCastContext context) {
        return checkContext(context, true, true, getManaCost(), true);
    }

    @Override
    public void postCast(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), getManaCost()*(getChargeTime()/getMaxLifetime()));
    }

    @Override
    public void postTick(SpellCastContext context) {
        // no-op
    }

    @Override
    public LivingEntity preTick(SpellCastContext context) {
        if (context.simulacrtumLifetime == -1) {
            return null;
        }

        return checkContext(context, true, false, 0, false);
    }

    @Override
    public void postExitSimulacrum(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), 0);
    }






}
