package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import org.jetbrains.annotations.Nullable;

public class ChargedSpell extends GatedSpell implements ISimulacrumSpell {
    public ChargedSpell() {
        this(defaultProperties());
    }

    protected ChargedSpell(SpellProperties properties) {
        super(properties);
    }

    protected static SpellProperties defaultProperties() {
        return Spell.defaultProperties()
                .withManaCost(0)
                .withCooldown(0)
                .withSpellName("Charged Spell")
                .withSimulacraThreshold(0)
                .withSimulacraMaxLifetime(0);
    }


    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SimulacraAttachment.setChanneling(
                context.target,
                context,
                this,
                0,
                getSimulacrumMaxLifetime());
    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        SimulacraAttachment.clearChanneling(
                context.target
        );
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        perform(SpellEventPhase.CAST, context, simulacrumData);
    }

    @Override
    public int getSimulacrumThreshold() {
        return properties.simulacraThreshold();
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return properties.simulacraMaxLifetime();
    }

    // HUD
    @Override
    public final float progress(SimulacrumData data) {
        return data.lifetime() / Math.max(1, data.maxLifetime());
    }

    @Override
    public final int color(float progress) {
        if (progress >= 0.8) {
            return 0xFFFF0000; // Red when approaching limit
        } else {
            return 0xFF0000FF; // Blue when charging
        }
    }

}
