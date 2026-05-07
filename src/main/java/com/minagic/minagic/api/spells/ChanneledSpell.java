package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import org.jetbrains.annotations.Nullable;

public class ChanneledSpell extends Spell implements ISimulacrumSpell {
    public ChanneledSpell() {
        this(defaultProperties());
    }

    protected ChanneledSpell(SpellProperties properties) {
        super(properties);
    }

    protected static SpellProperties defaultProperties() {
        return Spell.defaultProperties()
                .withSpellName("ChanneledAutonomousSpell")
                .withManaCost(20)
                .withCooldown(30)
                .withSimulacraThreshold(0)
                .withSimulacraMaxLifetime(-1);
    }


    // Lifecycle methods


    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SimulacraAttachment.setChanneling(
                context.target,
                context,
                this,
                getSimulacrumThreshold(),
                -1);
    }

    @Override
    public final void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op for channeled spells
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        SimulacraAttachment.clearChanneling(context.target);
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op for channeled spells
    }


    @Override
    public int getSimulacrumThreshold() {
        return properties.simulacraThreshold();
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return properties.simulacraMaxLifetime();
    }

    @Override
    public final float progress(SimulacrumData data) {
        return data.lifetime() / data.threshold();
    }

    @Override
    public final int color(float progress) {
        return 0xFFFF00FF; // magenta for channeled spells
    }

}
