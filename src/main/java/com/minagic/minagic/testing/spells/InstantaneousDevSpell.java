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

    public InstantaneousDevSpell() {
        super();

        this.spellName = "InstantaneousDevSpell";
        this.manaCost = 10;
        this.cooldown = 40;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 0;
    }

    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}