package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChanneledAutonomousSpell extends Spell {
    @Override
    public final int getMaxLifetime() {
        return -1; // Channeled spells have no max lifetime
    }

    @Override
    public int getSimulacrumThreshold() {
        return 0; // Channeled autonomous cannot be autocast by simulacra
    }

    @Override
    public String getString() {
        return "Channeled Autonomous Spell";
    }

    @Override
    public int getManaCost() {
        return 20; // default mana cost
    }

    @Override
    public int getCooldownTicks() {
        return 30; // default cooldown
    }

    // lifecycle like of channelled spell

    // pre* methods

    @Override
    public final boolean preStart(SpellCastContext context) {
        return validateContext(context) &&
                validateCaster(context) &&
                validateCooldown(context) &&
                validateItem(context);
    }

    @Override
    public final boolean preTick(SpellCastContext context) {return false;}

    @Override
    public final boolean preStop(SpellCastContext context) {
        return validateContext(context) &&
                validateCaster(context) &&
                validateCooldown(context) &&
                validateItem(context); }

    @Override
    public final boolean preExitSimulacrum(SpellCastContext context) {return false;}

    @Override
    public final boolean preCast(SpellCastContext context) {
        return validateContext(context) &&
                validateCaster(context) &&
                validateCooldown(context) &&
                validateMana(context, getManaCost()) &&
                validateItem(context);}

    // post* methods

    @Override
    public final void postStart(SpellCastContext context) {}

    @Override
    public final void postTick(SpellCastContext context) {}

    @Override
    public final void postStop(SpellCastContext context) {}

    @Override
    public final void postCast(SpellCastContext context) {
        drainMana(context, getManaCost());
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context) {

        applyCooldown(context, getCooldownTicks());
    }

    @Override
    public final void start(SpellCastContext context) {
        PlayerSimulacraAttachment.setActiveChanneling(context, this, getSimulacrumThreshold(), -1);

    }

    @Override
    public final void tick(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context) {
        PlayerSimulacraAttachment.clearChanneling(context.target);

    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {}

    @Override
    public final float progress(SimulacrumSpellData data) {
        if (data.maxLifetime() <= 0) {
            return 1f;
        }
        return data.remainingLifetime() / data.maxLifetime();
    }

    @Override
    public final int color(float progress) {
        return 0xFFAAAAFF;
    }


}
