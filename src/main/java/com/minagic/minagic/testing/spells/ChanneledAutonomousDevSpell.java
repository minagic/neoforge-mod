package com.minagic.minagic.testing.spells;


import com.minagic.minagic.abstractionLayer.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class ChanneledAutonomousDevSpell extends ChanneledAutonomousSpell {

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) return;

        System.out.println("[ChanneledAutonomousDevSpell] 🔫 Pew Pew Pew! lifetime="
                + context.simulacrtumLifetime + " Player=" + context.caster.getName().getString());

        applyMagicCosts(context);

    }

    @Override
    public int getManaCost() { return 2; }

    @Override
    public int getCooldownTicks() { return 5; }

    @Override
    public String getString() { return "ChanneledAutonomousDevSpell"; }
}
