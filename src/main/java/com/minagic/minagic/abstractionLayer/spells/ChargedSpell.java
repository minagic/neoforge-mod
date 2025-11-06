package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
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
        return 0; // Default max lifetime for charged spells
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
        return 0; // Default mana cost for charged spells
    }

    // implement pre* methods
    @Override
    public final boolean preStart(SpellCastContext context) {
        return checkContext(context, true, true, 0, true, false);
    }

    @Override
    public final boolean preTick(SpellCastContext context) {
        return checkContext(context, true, false, 0, true, true);
    }

    @Override
    public final boolean preStop(SpellCastContext context) {
        return checkContext(context, true, false, 0, true, false);
    }

    @Override
    public final boolean preExitSimulacrum(SpellCastContext context) {
        return checkContext(context, true, false, 0, true, false);
    }

    @Override
    public final boolean preCast(SpellCastContext context) {
        return checkContext(context, true, true, getManaCost(), true, true);
    }

    // implement post* methods

    @Override
    public final void postStart(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void postTick(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void postStop(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), 0); // when dropped only apply cooldown
    }

    @Override
    public final void postCast(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), getManaCost());
        PlayerSimulacraAttachment.clearChanneling(context.target);
    }

    // lifecycle methods
    @Override
    public final void start(SpellCastContext context) {
        PlayerSimulacraAttachment.setActiveChanneling(
                context,
                this,
                0,
                getMaxLifetime()
        );
    }

    @Override
    public void tick(SpellCastContext context) {
        System.out.println("Charging spell: " + getString() + " | Charge time: " + chargeTime);
        chargeTime = context.simulacrtumLifetime;
    }

    @Override
    public final void stop(SpellCastContext context) {

        PlayerSimulacraAttachment.clearChanneling(
                context.target
        );


    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {
        onCast(context);
    }


}
