package com.minagic.minagic.testing.spells;
import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class AutonomousDevSpell extends AutonomousSpell {

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) return;

        System.out.println("[AutonomousDevSpell] 🔄 Tick fired for "
                + context.caster.getName().getString());

        applyMagicCosts(context);
    }

    @Override
    public int getManaCost() { return 1; }

    @Override
    public int getCooldownTicks() { return 0; }

    @Override
    public int getSimulacrumThreshold() { return 20; } // once per second

    @Override
    public String getString() { return "AutonomousDevSpell"; }
}
