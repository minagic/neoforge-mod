package com.minagic.minagic.spellgates;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellGateChain {
    final List<ISpellGate> gates = new ArrayList<>();
    SpellEffect effect;
    Spell spell;

    public SpellGateChain(Spell spell){
        this.spell = spell;
    }

    public SpellGateChain addGate(ISpellGate gate) {
        gates.add(gate);
        return this;
    }

    public SpellGateChain setEffect(SpellEffect effect) {
        this.effect = effect;
        return this;
    }


    public void execute(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        for (ISpellGate gate : gates) {
            if (gate.getGatePhase() == ISpellGate.GatePhase.GAMEPLAY && spell.isTechnical()) {
                Minagic.LOGGER.trace(
                        "Skipping gameplay gate check {} for technical spell {}",
                        gate.getClass().getSimpleName(),
                        spell.getID()
                );
                continue;
            }
            if (!gate.check(ctx, simData)) {
                gate.onFail(ctx, simData);
                return;
            }
        }

        if (effect != null) {
            effect.execute(ctx, simData);
        }

        for (ISpellGate gate : gates) {
            if (gate.getGatePhase() == ISpellGate.GatePhase.GAMEPLAY && spell.isTechnical()) {
                Minagic.LOGGER.trace(
                        "Skipping gameplay gate post-action {} for technical spell {}",
                        gate.getClass().getSimpleName(),
                        spell.getID()
                );
            }
            gate.post(ctx, simData);
        }

    }

    @FunctionalInterface
    public interface SpellEffect {
        void execute(SpellCastContext ctx, @Nullable SimulacrumData simData);
    }

}
