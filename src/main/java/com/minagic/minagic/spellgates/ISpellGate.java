package com.minagic.minagic.spellgates;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;

import javax.annotation.Nullable;

public interface ISpellGate {
    /**
     * SAFETY gates enforce runtime correctness and must never
     * be bypassed, even for technical/internal spells.
     *
     * GAMEPLAY gates enforce balancing/resource/gameplay rules
     * and may be skipped for technical spells.
     */
    enum GatePhase {
        SAFETY,
        GAMEPLAY
    }

    default GatePhase getGatePhase(){return GatePhase.GAMEPLAY;}

    boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData);

    // Optional fallback if check fails
    default void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
    }

    // Optional postEffect-action if main spell is cast
    default void postEffect(SpellCastContext ctx, @Nullable SimulacrumData simData) {
    }

    abstract class SafetySpellGate implements ISpellGate {
        @Override
        public final GatePhase getGatePhase() {
            return GatePhase.SAFETY;
        }
    }

    String describe();
}
