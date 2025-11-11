package com.minagic.minagic.testing.spells;
import com.minagic.minagic.abstractionLayer.spells.AutonomousChargedSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class AutonomousChargedDevSpell extends AutonomousChargedSpell {

    @Override
    public void cast(SpellCastContext context) {
        System.out.println("[AutonomousChargedDevSpell] 💥 Detonated after delay! "
                + context.caster.getName().getString());

    }

    @Override
    public int getManaCost() { return 15; }

    @Override
    public int getCooldownTicks() { return 100; }

    @Override
    public int getSimulacrumThreshold() { return 60; } // 3s charge period

    @Override
    public String getString() { return "AutonomousChargedDevSpell"; }

    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}