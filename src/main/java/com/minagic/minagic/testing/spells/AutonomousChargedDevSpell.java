package com.minagic.minagic.testing.spells;
import com.minagic.minagic.abstractionLayer.spells.AutonomousChargedSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class AutonomousChargedDevSpell extends AutonomousChargedSpell {

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) return;

        System.out.println("[AutonomousChargedDevSpell] 💥 Detonated after delay! "
                + context.caster.getName().getString());

        applyMagicCosts(context);
    }

    @Override
    public int getManaCost() { return 15; }

    @Override
    public int getCooldownTicks() { return 100; }

    @Override
    public int getSimulacrumThreshold() { return 60; } // 3s charge period

    @Override
    public String getString() { return "AutonomousChargedDevSpell"; }
}