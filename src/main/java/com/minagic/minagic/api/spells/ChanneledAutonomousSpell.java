package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
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
        SimulacraAttachment.setChanneling(context.target, context, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());

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
    public void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {}


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
        System.out.println("[ChanneledAutonomousSpell] Progress: " + data.remainingLifetime() / data.maxLifetime());
        return data.remainingLifetime() / data.maxLifetime();

    }

    @Override
    public final int color(float progress) {
        return 0xFFAAAAFF;
    }


}
