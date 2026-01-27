package com.minagic.minagic.spellgates;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;

import javax.annotation.Nullable;

public interface ISpellGate {
    boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData);

    // Optional fallback if check fails
    default void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
    }

    // Optional post-action if main spell is cast
    default void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
    }
}
