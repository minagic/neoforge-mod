package com.minagic.minagic.spellgates;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellGateChain {
    /**
     * SpellGateChain execution model:
     *
     * 1. Execute all gate checks in order
     * 2. First failing gate aborts chain
     * 3. Failing gate owns failure handling
     * 4. Effect executes only if all gates pass
     * 5. All gates receive postEffect() after successful execution
     */
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
                        "Skipping gameplay gate postEffect-action {} for technical spell {}",
                        gate.getClass().getSimpleName(),
                        spell.getID()
                );
                continue;
            }
            gate.postEffect(ctx, simData);
        }

    }

    @FunctionalInterface
    public interface SpellEffect {
        void execute(SpellCastContext ctx, @Nullable SimulacrumData simData);
    }

    public String describe(){
        StringBuilder result = new StringBuilder();
        for (ISpellGate gate: this.gates){
            result.append(gate.describe()).append("\n");
        }
        return result.toString();
    }
}
