package com.minagic.minagic.testing.spells;


import com.minagic.minagic.Config;
import com.minagic.minagic.abstractionLayer.spells.ChanneledSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChanneledDevSpell extends ChanneledSpell {

    @Override
    public void cast(SpellCastContext context) {

        System.out.println("[ChanneledDevSpell] 🔫 Pew! " + context.caster.getName().getString());
    }

    public ChanneledDevSpell() {
        super();

        this.spellName = "ChanneledDevSpell";
        this.manaCost = 40;
        this.cooldown = 10;

        this.simulacraThreshold = 5;       // fire every 5 ticks
        this.simulacraMaxLifetime = -1;    // channeled = infinite lifetime
    }
    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? CastFailureReason.OK : CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
