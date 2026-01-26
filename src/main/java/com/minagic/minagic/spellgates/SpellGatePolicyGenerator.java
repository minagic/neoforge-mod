package com.minagic.minagic.spellgates;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;

import javax.annotation.Nullable;
import java.util.List;

public class SpellGatePolicyGenerator {

    public static SpellGateChain build(
            SpellEventPhase phase,
            List<DefaultGates.ClassGate.AllowedClass> allowedClasses,
            @Nullable Integer cooldownTicks,
            @Nullable Integer manaCostOnCast,
            @Nullable Integer manaSustainPerTick,
            boolean requireSimulacrumOnCast,
            Spell spell
    ) {
        SpellGateChain chain = new SpellGateChain();

        switch (phase) {
            case START -> {
                if (cooldownTicks != null)
                    chain.addGate(new DefaultGates.CooldownGate(spell, cooldownTicks));
                if (!allowedClasses.isEmpty())
                    chain.addGate(new DefaultGates.ClassGate(allowedClasses));
            }
            case CAST -> {
                if (!allowedClasses.isEmpty())
                    chain.addGate(new DefaultGates.ClassGate(allowedClasses));
                if (manaCostOnCast != null)
                    chain.addGate(new DefaultGates.ManaGate(manaCostOnCast, spell));
                if (requireSimulacrumOnCast)
                    chain.addGate(new DefaultGates.SimulacrumGate());
            }
            case TICK -> {
                if (!allowedClasses.isEmpty())
                    chain.addGate(new DefaultGates.ClassGate(allowedClasses));
                if (manaSustainPerTick != null && manaSustainPerTick > 0)
                    chain.addGate(new DefaultGates.ManaSustainGate(manaSustainPerTick));
                chain.addGate(new DefaultGates.SimulacrumGate());
            }
            case EXIT_SIMULACRUM -> {
                chain.addGate(new DefaultGates.SimulacrumGate());
            }
            case STOP -> {
                // intentionally left empty
            }
        }

        return chain;
    }
}
