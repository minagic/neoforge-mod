package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import org.jetbrains.annotations.Nullable;

/**
 * A simple self-managed spell that automatically attaches or detaches itself
 * as a background simulacrum when cast. Runs indefinitely (maxLifetime = -1)
 * until toggled again.
 */
public class AutonomousSpell extends Spell implements ISimulacrumSpell {


    @Override
    public void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.START, this.getAllowedClasses(), null, this.manaCost, 0, false, this).setEffect(
                ((ctx, simData) -> {

                    SimulacraAttachment sim = ctx.target.getData(ModAttachments.PLAYER_SIMULACRA.get());


                    boolean existing = sim.hasSpell(ModSpells.getId(this));

                    if (existing) {
                        SimulacraAttachment.removeSimulacrum(ctx.target, ModSpells.getId(this));
                    } else {
                        new SpellGateChain()
                                .addGate(new DefaultGates.CooldownGate(this, cooldown))
                                .setEffect(
                                        (internal_ctx, internal_data) -> {
                                            SimulacraAttachment.addSimulacrum(internal_ctx.target, internal_ctx, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
                                        }
                                )
                                .execute(ctx, simData);
                    }
                })

        ).execute(context, simulacrumData);


    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        // No-op for autonomous spells
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        // No-op for autonomous spells
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
        return data.remainingLifetime() / data.maxLifetime();
    }

    @Override
    public final int color(float progress) {
        return 0xFFAAFFAA;
    }

}
