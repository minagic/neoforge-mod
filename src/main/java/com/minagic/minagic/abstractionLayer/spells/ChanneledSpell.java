package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class ChanneledSpell extends Spell {

    @Override
    public final int getMaxLifetime() {
        return -1; // Channeled spells have no max lifetime
    }

    @Override
    public final boolean preStart(SpellCastContext context) {
        return checkContext(context, true, true, 0, true, false);
    }

    @Override
    public final boolean preTick(SpellCastContext context) {
        return false;
    }


    @Override
    public final boolean preStop(SpellCastContext context) {
        return checkContext(context, true, false, 0, true, false);
    }


    @Override
    public final boolean preCast(SpellCastContext context) {
        return checkContext(context, true, true, getManaCost(), true, false);
    }


    @Override
    public final boolean preExitSimulacrum(SpellCastContext context) {
        return checkContext(context, true, false, 0, true, false);
    }

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
    public final void postCast(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), getManaCost());
        PlayerSimulacraAttachment.clearChanneling(context.target);
    }


    @Override
    public final void postExitSimulacrum(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), 0);
    }

    // Lifecycle methods


    @Override
    public final void start(SpellCastContext context) {
        PlayerSimulacraAttachment.setActiveChanneling(
                context,
                this,
                getSimulacrumThreshold(),
                -1);
    }

    @Override
    public final void tick(SpellCastContext context) {
        // no-op for channeled spells
    }
    @Override
    public final void stop(SpellCastContext context) {
        PlayerSimulacraAttachment.clearChanneling(context.target);
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {
        // no-op for channeled spells
    }

    @Override
    public final float progress(SimulacrumSpellData data) {
        return data.lifetime()/data.threshold();
    }

    @Override
    public final int color(float progress) {
        return 0xFFFF00FF; // magenta for channeled spells
    }

}
