package com.minagic.minagic.api.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import org.jetbrains.annotations.Nullable;

public class ChanneledAutonomousSpell extends Spell implements ISimulacrumSpell {
    public ChanneledAutonomousSpell() {
        super();

        this.spellName = "ChanneledAutonomousSpell";
        this.manaCost = 20;
        this.cooldown = 30;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = -1; // no max lifetime
    }

    // lifecycle like of channelled spell

    @Override
    public void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.START, this.getAllowedClasses(), this.cooldown, this.manaCost, 0, false, this).setEffect(
                ((ctx, simData) -> {
                    SimulacraAttachment.setChanneling(ctx.target, ctx, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
                })
        ).execute(context, simulacrumData);


    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op
    }

    @Override
    public void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        SimulacraAttachment.clearChanneling(context.target);
    }

    @Override
    public void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
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
        if (data.maxLifetime() <= 0) {
            return 1f;
        }
        float progress = data.remainingLifetime() / data.maxLifetime();
        Minagic.LOGGER.trace("ChanneledAutonomousSpell progress: {}", progress);
        return progress;

    }

    @Override
    public final int color(float progress) {
        return 0xFFAAAAFF;
    }


}
