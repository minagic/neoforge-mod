package com.minagic.minagic.testing.spells;


import com.minagic.minagic.abstractionLayer.spells.ChanneledSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class ChanneledDevSpell extends ChanneledSpell {

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) return;

        System.out.println("[ChanneledDevSpell] 🔫 Pew! " + context.caster.getName().getString());

        applyMagicCosts(context);
    }

    @Override
    public int getManaCost() { return 40; }

    @Override
    public int getCooldownTicks() { return 10; } // no delay

    @Override
    public int getSimulacrumThreshold() { return 5; } // fire every 5 ticks

    @Override
    public String getString() { return "ChanneledDevSpell"; }
}
