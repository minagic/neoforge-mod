package com.minagic.minagic.testing.spells;

import com.minagic.minagic.abstractionLayer.spells.ChargedSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class ChargedDevSpell extends ChargedSpell {

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) return;

        float chargeRatio = (float) getChargeTime() / getMaxLifetime();
        System.out.println("[ChargedDevSpell] ⚡ BOOM! ChargeRatio=" + chargeRatio
                + " Player=" + context.caster.getName().getString());

        applyMagicCosts(context);
    }

    @Override
    public int getManaCost() { return 30; }

    @Override
    public int getCooldownTicks() { return 60; } // ~3s

    @Override
    public int getMaxLifetime() { return 100; } // charge time (5s at 20 TPS)

    @Override
    public String getString() { return "ChargedDevSpell"; }
}
