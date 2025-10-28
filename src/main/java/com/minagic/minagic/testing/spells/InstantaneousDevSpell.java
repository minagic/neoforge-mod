package com.minagic.minagic.testing.spells;

import com.minagic.minagic.abstractionLayer.spells.InstanteneousSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class InstantaneousDevSpell extends InstanteneousSpell {

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) return;

        System.out.println("[InstantaneousDevSpell] Cast by " + context.caster.getName().getString()
                + " at " + context.level.dimension().location());

        applyMagicCosts(context);
    }

    @Override
    public int getManaCost() { return 10; }

    @Override
    public int getCooldownTicks() { return 40; } // 2 seconds

    @Override
    public String getString() { return "InstantaneousDevSpell"; }
}