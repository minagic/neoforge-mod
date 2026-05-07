package com.minagic.minagic.spellgates;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public interface ISpellGate {

    enum GatePhase {
        SAFETY,
        GAMEPLAY
    }

    default GatePhase getGatePhase(){return GatePhase.GAMEPLAY;}

    boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData);

    // Optional fallback if check fails
    default void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
    }

    // Optional post-action if main spell is cast
    default void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
    }

    abstract class SafetySpellGate implements ISpellGate {
        @Override
        public final GatePhase getGatePhase() {
            return GatePhase.SAFETY;
        }
    }

    String describe();
}
