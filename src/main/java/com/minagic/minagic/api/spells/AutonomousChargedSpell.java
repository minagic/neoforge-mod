package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import org.jetbrains.annotations.Nullable;

/// / An abstract class representing spells that are charged up over time before being released.
public class AutonomousChargedSpell extends GatedSpell implements ISimulacrumSpell {
    public AutonomousChargedSpell() {
        this(defaultProperties());
    }

    protected AutonomousChargedSpell(SpellProperties properties) {
        super(properties);
    }

    protected static SpellProperties defaultProperties() {
        return Spell.defaultProperties()
                .withSpellName("AutonomousChargedSpell")
                .withManaCost(0)
                .withCooldown(0)
                .withSimulacraThreshold(0)
                .withSimulacraMaxLifetime(0);
    }

    @Override
    public int getSimulacrumThreshold() {
        return properties.simulacraThreshold();
    }

    @Override
    public final int getSimulacrumMaxLifetime() {
        return getSimulacrumThreshold();
    }

    @Override
    public SpellGateChain getGateChain(SpellEventPhase phase){
        if (phase == SpellEventPhase.START){
            return new SpellGateChain(this).addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));
        }
        return defaultGateChain(phase);
    }


    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        boolean existing = SimulacraAttachment.hasSpell(context.target, getID());

        if (existing) {
            SimulacraAttachment.removeSimulacrum(context.target, getID());
        } else {
            new SpellGateChain(this)
                    .addGate(new DefaultGates.CooldownGate(this, properties.cooldown()))
                    .setEffect(
                            (internal_ctx, internal_data) -> {
                                SimulacraAttachment.addSimulacrum(internal_ctx.target, internal_ctx, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
                            }
                    )
                    .execute(context, simulacrumData);
        }

    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
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
