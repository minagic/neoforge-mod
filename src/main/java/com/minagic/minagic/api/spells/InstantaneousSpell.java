package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import org.jetbrains.annotations.Nullable;

/// An abstract class representing spells that take effect immediately upon casting.
/// To use, extend this class and implement the cast method, as well as getManaCost, getCooldownTicks and getString.
public class InstantaneousSpell extends Spell {
    public InstantaneousSpell() {
        this(defaultProperties());
    }

    protected InstantaneousSpell(SpellProperties properties) {
        super(properties);
    }

    protected static SpellProperties defaultProperties() {
        return Spell.defaultProperties()
                .withSpellName("InstantaneousSpell")
                .withManaCost(0)
                .withCooldown(0)
                .withSimulacraThreshold(0)
                .withSimulacraMaxLifetime(0);
    }


    // lifecycle methods
    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        perform(SpellEventPhase.CAST, context, null);

    }

    @Override
    public final void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        // no-op
    }

    @Override
    public final int color(float progress) {
        return 0x00000000; // transparent
    }

}
