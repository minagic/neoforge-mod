package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import org.jetbrains.annotations.Nullable;

public class ChanneledSpell extends Spell implements ISimulacrumSpell {
    public ChanneledSpell() {
        super();

        this.spellName = "ChanneledAutonomousSpell";
        this.manaCost = 20;
        this.cooldown = 30;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = -1; // no max lifetime
    }


    // Lifecycle methods


    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.START, this.getAllowedClasses(), this.cooldown, this.manaCost, 0, false, this).setEffect(
                ((ctx, simData) -> {
                    SimulacraAttachment.setChanneling(
                            ctx.target,
                            ctx,
                            this,
                            getSimulacrumThreshold(),
                            -1);
                })
        ).execute(context, simulacrumData);

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
        return this.simulacraThreshold;
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return this.simulacraMaxLifetime;
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
