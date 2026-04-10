package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import org.jetbrains.annotations.Nullable;


public abstract class GatedSpell extends Spell {
    public record SpellPolicyData(
            @Nullable Integer cooldownTicks,
            @Nullable Integer manaCostOnCast,
            @Nullable Integer manaSustainPerTick,
            boolean requireSimulacrumOnCast
    ) {}


    @Override
    public void perform(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        if (context.validate()) {
            return;
        }

        if (simulacrumData != null && !simulacrumData.validate()) {
            return;
        }

        // --- Fetch gate chain for this phase ---
        SpellGateChain chain = getGateChain(phase);

        // --- If no gating, execute directly ---
        if (chain == null) {
            dispatchPhase(phase, context, simulacrumData);
            return;
        }

        // --- Execute through gate chain ---
        chain
                .setEffect((ctx, simData) -> dispatchPhase(phase, ctx, simData))
                .execute(context, simulacrumData);
    }

    /**
     * Override this to customize gating per phase.
     * Return null to skip gating entirely for that phase.
     */
    protected @Nullable SpellGateChain getGateChain(SpellEventPhase phase) {
        return defaultGateChain(phase);
    }

    protected SpellGateChain defaultGateChain(SpellEventPhase phase) {
        return buildPolicy(phase, getPolicyData(phase));
    }

    protected SpellPolicyData getPolicyData(SpellEventPhase phase) {
        return switch (phase) {
            case START -> new SpellPolicyData(cooldown, null, null, false);
            case CAST -> new SpellPolicyData(null, manaCost, null, false);
            case TICK -> new SpellPolicyData(null, null, sustainCost, false);
            default -> new SpellPolicyData(null, null, null, false);
        };
    }

    protected SpellGateChain buildPolicy(SpellEventPhase phase, SpellPolicyData data) {
        SpellGateChain chain = new SpellGateChain(this);

        switch (phase) {
            case START -> {
                if (data.cooldownTicks() != null)
                    chain.addGate(new DefaultGates.CooldownGate(this, data.cooldownTicks()));

                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));
            }

            case CAST -> {
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));

                if (data.manaCostOnCast() != null)
                    chain.addGate(new DefaultGates.PowerSourceCostGate(data.manaCostOnCast(), this));

                if (data.requireSimulacrumOnCast())
                    chain.addGate(new DefaultGates.SimulacrumGate());
            }

            case TICK -> {
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));

                if (data.manaSustainPerTick() != null && data.manaSustainPerTick() > 0)
                    chain.addGate(new DefaultGates.PowerSourceSustainGate(data.manaSustainPerTick()));

                chain.addGate(new DefaultGates.SimulacrumGate());
            }

            case EXIT_SIMULACRUM -> {
                chain.addGate(new DefaultGates.SimulacrumGate());
            }

            case STOP -> {
                // nothing
            }
        }

        return chain;
    }
}