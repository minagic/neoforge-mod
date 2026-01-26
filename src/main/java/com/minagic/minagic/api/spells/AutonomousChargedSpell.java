package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.utilities.SpellValidationResult;
import org.jetbrains.annotations.Nullable;

//// An abstract class representing spells that are charged up over time before being released.
public class AutonomousChargedSpell extends Spell implements ISimulacrumSpell {
    public AutonomousChargedSpell() {
        super();

        this.spellName = "AutonomousChargedSpell";
        this.manaCost = 0;
        this.cooldown = 0;

        // Lifetime equals threshold in original behavior:
        // maxLifetime = simulacrumThreshold, but since you initialize by constructor,
        // we set both here.
        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 0;
    }

    @Override
    public int getSimulacrumThreshold() {
        return simulacraThreshold;
    }

    @Override
    public final int getSimulacrumMaxLifetime() {
        return getSimulacrumThreshold();
    }


    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.START, this.getAllowedClasses(), this.cooldown, this.manaCost, 0, false, this).setEffect(
                ((ctx, simData) -> {
                    SimulacraAttachment sim = ctx.target.getData(ModAttachments.PLAYER_SIMULACRA.get());

                    boolean existing = sim.hasSpell(ModSpells.getId(this));

                    if (existing) {
                        SimulacraAttachment.removeSimulacrum(ctx.target, ModSpells.getId(this));
                    } else {
                        SimulacraAttachment.addSimulacrum(ctx.target, ctx, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
                    }
                })

        ).execute(context, simulacrumData);

    }

    @Override
    public  final void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op
    }


    @Override
    public final float progress(SimulacrumData data) {
        return data.remainingLifetime() / data.maxLifetime();
    }

    @Override
    public final int color(float progress) {
        return 0xFFFFFFAA;
    }


}
