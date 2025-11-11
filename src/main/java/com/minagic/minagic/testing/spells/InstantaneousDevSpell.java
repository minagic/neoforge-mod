package com.minagic.minagic.testing.spells;

import com.minagic.minagic.abstractionLayer.spells.InstanteneousSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class InstantaneousDevSpell extends InstanteneousSpell {

    @Override
    public void cast(SpellCastContext context) {

        System.out.println("[InstantaneousDevSpell] Cast by " + context.caster.getName().getString()
                + " at " + context.level().dimension().location());
    }

    @Override
    public int getManaCost() { return 10; }

    @Override
    public int getCooldownTicks() { return 40; } // 2 seconds

    @Override
    public String getString() { return "InstantaneousDevSpell"; }

    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}