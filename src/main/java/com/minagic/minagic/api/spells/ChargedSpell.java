package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import org.jetbrains.annotations.Nullable;

public class ChargedSpell extends Spell implements ISimulacrumSpell {
    public ChargedSpell() {
        super(); // keep whatever superclass initialization you rely on

        this.manaCost = 0;               // default for charged spells
        this.cooldown = 0;               // stays default unless you override elsewhere
        this.spellName = "Charged Spell";
        this.simulacraThreshold = 0;     // cannot be autocast
        this.simulacraMaxLifetime = 0;   // default max lifetime for charged spells
    }


    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.START, this.getAllowedClasses(), this.cooldown, this.manaCost, 0, false, this).setEffect(
                ((ctx, simData) -> SimulacraAttachment.setChanneling(
                        ctx.target,
                        ctx,
                        this,
                        0,
                        getSimulacrumMaxLifetime()
                ))
        ).execute(context, simulacrumData);
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
        new SpellGateChain().addGate(new DefaultGates.SimulacrumGate()).setEffect((ctx, simData) -> perform(SpellEventPhase.CAST, ctx, simData)).execute(context, simulacrumData);


    }

    @Override
    public int getSimulacrumThreshold() {
        return this.simulacraThreshold;
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return this.simulacraMaxLifetime;
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
