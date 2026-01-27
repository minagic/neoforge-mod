package com.minagic.minagic.spellgates;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellGateChain {
    List<ISpellGate> gates = new ArrayList<>();
    SpellEffect effect;

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
            if (!gate.check(ctx, simData)) {
                gate.onFail(ctx, simData);
                return;
            }
        }

        if (effect != null) {
            effect.execute(ctx, simData);
        }

        for (ISpellGate gate : gates) {
            gate.post(ctx, simData);
        }

    }

    @FunctionalInterface
    public interface SpellEffect {
        void execute(SpellCastContext ctx, @Nullable SimulacrumData simData);
    }

}
