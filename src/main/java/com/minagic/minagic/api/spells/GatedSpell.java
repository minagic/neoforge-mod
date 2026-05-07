package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import org.jetbrains.annotations.Nullable;


public abstract class GatedSpell extends Spell {
    protected GatedSpell() {
        super();
    }

    protected GatedSpell(SpellProperties properties) {
        super(properties);
    }

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
        return buildPolicy(phase);
    }

    protected SpellGateChain buildPolicy(SpellEventPhase phase) {
        SpellGateChain chain = new SpellGateChain(this);

        switch (phase) {
            case START -> {
                chain.addGate(new DefaultGates.CooldownGate(this, properties.cooldown()));

                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));
            }

            case CAST -> {
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));

                chain.addGate(new DefaultGates.PowerSourceCostGate(properties.manaCost(), this));

                if (properties.requireSimulacrumOnCast())
                    chain.addGate(new DefaultGates.SimulacrumGate());
            }

            case TICK -> {
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(this));

                if (properties.sustainCost() > 0)
                    chain.addGate(new DefaultGates.PowerSourceSustainGate(properties.sustainCost()));

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
