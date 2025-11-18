package com.minagic.minagic.testing.spells;

import com.minagic.minagic.abstractionLayer.spells.ChargedSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChargedDevSpell extends ChargedSpell {

    @Override
    public void cast(SpellCastContext context) {
        float chargeRatio = (float) context.simulacrtumLifetime.lifetime() / context.simulacrtumLifetime.maxLifetime();
        System.out.println("[ChargedDevSpell] ⚡ BOOM! ChargeRatio=" + chargeRatio
                + " Player=" + context.caster.getName().getString());
    }

    public ChargedDevSpell() {
        super();

        this.spellName = "ChargedDevSpell";
        this.manaCost = 30;
        this.cooldown = 60;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 100; // charge time / max lifetime
    }
    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
