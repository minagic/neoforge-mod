package com.minagic.minagic.testing.spells;
import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class AutonomousDevSpell extends AutonomousSpell {

    @Override
    public void cast(SpellCastContext context) {

        System.out.println("[AutonomousDevSpell] 🔄 Tick fired for "
                + context.caster.getName().getString());

    }

    @Override
    public int getManaCost() { return 40; }

    @Override
    public int getCooldownTicks() { return 10; }

    @Override
    public int getSimulacrumThreshold() { return 20; } // once per second

    @Override
    public String getString() { return "AutonomousDevSpell"; }
}
