package com.minagic.minagic.spellgates;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;

import javax.annotation.Nullable;

public class SpellGatePolicyGenerator {

    public static SpellGateChain build(
            SpellEventPhase phase,
            @Nullable Integer cooldownTicks,
            @Nullable Integer manaCostOnCast,
            @Nullable Integer manaSustainPerTick,
            boolean requireSimulacrumOnCast,
            Spell spell
    ) {
        SpellGateChain chain = new SpellGateChain(spell);

        switch (phase) {
            case START -> {
                if (cooldownTicks != null)
                    chain.addGate(new DefaultGates.CooldownGate(spell, cooldownTicks));
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(spell));
            }
            case CAST -> {
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(spell));
                if (manaCostOnCast != null)
                    chain.addGate(new DefaultGates.PowerSourceCostGate(manaCostOnCast, spell));
                if (requireSimulacrumOnCast)
                    chain.addGate(new DefaultGates.SimulacrumGate());
            }
            case TICK -> {
                chain.addGate(new DefaultGates.PowerSourcePrerequisiteGate(spell));
                if (manaSustainPerTick != null && manaSustainPerTick > 0)
                    chain.addGate(new DefaultGates.PowerSourceSustainGate(manaSustainPerTick));
                chain.addGate(new DefaultGates.SimulacrumGate());
            }
            case EXIT_SIMULACRUM -> chain.addGate(new DefaultGates.SimulacrumGate());
            case STOP -> {
            }
        }

        return chain;
    }
}
